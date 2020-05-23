package com.example.acmcovidapplication.services;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.app.job.JobScheduler;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.example.acmcovidapplication.R;
import com.example.acmcovidapplication.Util;
import com.example.acmcovidapplication.broadcast_receiver.AlarmReceiver;
import com.example.acmcovidapplication.broadcast_receiver.NetworkStateReceiver;
import com.example.acmcovidapplication.db.DatabaseHelper;
import com.example.acmcovidapplication.db.DeviceModel;
import com.example.acmcovidapplication.db.FirebaseHelper;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.BeaconTransmitter;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.bluetooth.le.AdvertiseSettings.ADVERTISE_TX_POWER_LOW;
import static com.example.acmcovidapplication.App.CHANNEL_ID;
import static com.example.acmcovidapplication.Util.setBluetooth;


public class CustomService extends Service implements BeaconConsumer, LifecycleOwner,
        NetworkStateReceiver.NetworkStateReceiverListener {
    private BeaconManager beaconManager;
    BeaconTransmitter beaconTransmitter;
    private static final int FOREGROUND_ID = 1;
    private BackgroundPowerSaver backgroundPowerSaver;
    private double MAX_DISTANCE;
    private int SCAN_PERIOD;
    private int TIME_BETWEEB_TWO_SCAN;
    public static final String BEACON_LAYOUT = "m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25";
    public String ALLOWED;

    private DatabaseHelper database_helper;
    public static final String TAG = "DB_CHECKER";

    private final NetworkStateReceiver networkStateReceiver = new NetworkStateReceiver();
    String deviceId;
    private LocationManager locationManager;

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            //your code here
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate() {
        super.onCreate();
        setResources();

        locationManager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5 * 60 * 1000, 100, mLocationListener);
            return;
        }

        networkStateReceiver.addListener(this);
        registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));


        Calendar c = Calendar.getInstance(); //gives u calendar with current time
        c.add(Calendar.SECOND, 30);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
        if (alarmManager != null) {
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(),
                    AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);

        }
        //deviceRepository = new DeviceRepository(this);
        database_helper = DatabaseHelper.getInstance(this);
        deviceId = DatabaseHelper.getInstance(this).getUserId();


        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);

        //int result = BeaconTransmitter.checkTransmissionSupported(this); // this return device supports to Transmit
        backgroundPowerSaver = new BackgroundPowerSaver(this);


    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo_4)
                .setContentTitle(this.getResources().getString(R.string.app_name) + " is active")
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),R.mipmap.app_icon))
                .setStyle(new NotificationCompat.BigTextStyle().bigText("Keeping this app running will save you from becoming a COVID-19 victim"))
                .build();

        startForeground(FOREGROUND_ID, notification);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Util.scheduleJobHelper(this);
        }

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Log.d(TAG, "onCreate: devise bluetooth not supported");
        } else if (!mBluetoothAdapter.isEnabled()) {
            Log.d(TAG, "onCreate: bluetooth is disabled");
            setBluetooth(true);
        } else {
            if (database_helper.getAllowed()
                    && deviceId != null) {
                setupBeacon(deviceId);
            } else {
                if (deviceId == null) {
                    deviceId = database_helper.getUserId();
                    setupBeacon(deviceId);
                } else {
                    stopSelf();
                }
            }


        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
        alarmManager.cancel(pendingIntent);

        JobScheduler scheduler ;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
            scheduler.cancel(123);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.removeAllRangeNotifiers();
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @RequiresPermission(anyOf = {ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION})
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                for (Beacon beacon : beacons) {
                    Log.d(TAG, "I see a beacon ");
                    double latitude = 0, longitude = 0;

                    if (beacon.getDistance() < MAX_DISTANCE) {
                        Log.d(TAG, "I see a beacon that is less than" + MAX_DISTANCE + " meters away.");
                        //deviceRepository.insert(new Device( beacon.getId1().toString(), System.currentTimeMillis()));
                        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
                                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

                             Location location =  locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                             latitude = location.getLatitude();
                             longitude = location.getLongitude();
                            // Perform distance-specific action here
                        }


                        database_helper.addDevice(beacon.getId1().toString(),latitude,longitude);
                    }
                }
            }
        });

        try {
            beaconManager.setForegroundScanPeriod(SCAN_PERIOD);
            beaconManager.setForegroundBetweenScanPeriod(TIME_BETWEEB_TWO_SCAN);
            beaconManager.updateScanPeriods();
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
        }
    }

    //this will transmit the beacon

    private void setupBeacon(String deviceId) {
        BeaconParser beaconParser = new BeaconParser()
                .setBeaconLayout(BEACON_LAYOUT);
        if (beaconTransmitter == null) {
            Beacon beacon = new Beacon.Builder()
                    .setId1(deviceId) // need to generate ids device specific
                    .setId2("1")
                    .setId3("2")
                    .setManufacturer(0x0118)
                    .setTxPower(-81)
                    .setDataFields(Collections.singletonList(0L))
                    .build();

            beaconTransmitter = new BeaconTransmitter(this, beaconParser);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                beaconTransmitter.setAdvertiseTxPowerLevel(ADVERTISE_TX_POWER_LOW);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                beaconTransmitter.startAdvertising(beacon , new AdvertiseCallback(){
                    @Override
                    public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                        super.onStartSuccess(settingsInEffect);
                        Log.i(TAG, "Advertisement start succeeded.");
                    }

                    @Override
                    public void onStartFailure(int errorCode) {
                        super.onStartFailure(errorCode);
                        Log.e(TAG, "Advertisement start failed with code: "+errorCode);
                    }
                });
            }
        }
        if (beaconManager == null) {
            beaconManager = BeaconManager.getInstanceForApplication(this);

            // To detect proprietary beacons, you must add a line like below corresponding to your beacon
            // type.  Do a web search for "setBeaconLayout" to get the proper expression.
            beaconManager.getBeaconParsers().add(beaconParser);
            beaconManager.setEnableScheduledScanJobs(false);
       /* beaconManager.setBackgroundBetweenScanPeriod(0);
        beaconManager.setBackgroundScanPeriod(10000);*/
            beaconManager.bind(this);
        }

    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        //
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        //when ever user try to turn off bluetooth this method swill turn it on again
                        setBluetooth(true);
                        break;
                    case BluetoothAdapter.STATE_ON:
                        if (database_helper.getAllowed()
                                && deviceId != null) {
                            setupBeacon(deviceId);
                        } else {
                            if (deviceId == null) {
                                deviceId = database_helper.getUserId();
                                setupBeacon(deviceId);
                            } else {
                                stopSelf();
                            }
                        }

                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        //
                        break;
                }
            }
        }
    };


    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return null;
    }

    @Override
    public void networkAvailable() {
        new UploadTask().execute(this); // called after network state changed from disable to enable
    }

    @Override
    public void networkUnavailable() {
        Log.d(TAG, "networkUnavailable: ");
    }


    static class UploadTask extends AsyncTask<Context, Void, Context> {
        List<DeviceModel> list;

        @Override
        protected Context doInBackground(Context... contexts) {
            Context context = contexts[0];
            DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
            list = databaseHelper.getDevices();
            boolean isInternetConnectionAvailable = Util.isInternetAvailable();
            if (isInternetConnectionAvailable) {
                return context;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Context context) {
            super.onPostExecute(context);
            if (context != null) {
                DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);


                FirebaseHelper firebaseHelper = new FirebaseHelper();
                if (list != null && list.size() != 0) {
                    Log.d(TAG, "onPostExecute: firebase update method called");
                    for (DeviceModel deviceModel : list) {
                        firebaseHelper.update(deviceModel, databaseHelper);
                    }
                }

            }
        }
    }

    private void setResources() {
        MAX_DISTANCE = ResourcesCompat.getFloat(this.getResources(), R.dimen.max_distance);
        SCAN_PERIOD = this.getResources().getInteger(R.integer.scan_period);
        TIME_BETWEEB_TWO_SCAN = this.getResources().getInteger(R.integer.time_between_scan);
        ALLOWED = getResources().getString(R.string.is_allowed);
    }


}

