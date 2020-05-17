package com.example.acmcovidapplication.services;

import android.app.Notification;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.example.acmcovidapplication.R;
import com.example.acmcovidapplication.broadcast_receiver.NetworkStateReceiver;
import com.example.acmcovidapplication.db.DatabaseHelper;
import com.example.acmcovidapplication.db.DeviceModel;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.BeaconTransmitter;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;

import java.util.Arrays;
import java.util.Collection;

import javax.xml.transform.sax.TemplatesHandler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import static com.example.acmcovidapplication.App.CHANNEL_ID;
import static com.example.acmcovidapplication.Util.isInternetAvailable;
import static com.example.acmcovidapplication.Util.setBluetooth;


public class CustomService extends Service implements BeaconConsumer, LifecycleOwner, NetworkStateReceiver.NetworkStateReceiverListener {
    private BeaconManager beaconManager;
    private static final int FOREGROUND_ID = 1;
    private BackgroundPowerSaver backgroundPowerSaver;
    private  double MAX_DISTANCE          ;
    private  int    SCAN_PERIOD           ;
    private  int    TIME_BETWEEB_TWO_SCAN ;

    private DatabaseHelper database_helper;
    public static final String TAG = "DB_CHECKER";

    private final NetworkStateReceiver  networkStateReceiver = new NetworkStateReceiver();

    @Override
    public void onCreate() {
        super.onCreate();
        setResources();
        networkStateReceiver.addListener(this);
        registerReceiver(networkStateReceiver,new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));

        //deviceRepository = new DeviceRepository(this);
        database_helper = new DatabaseHelper(this);

        for(DeviceModel deviceModel: database_helper.getNotes()){
            Log.d(TAG, "onCreate: " +
                    "\nid- " + deviceModel.getID() +
                    "\nuser id - " + deviceModel.getUserID() +
                    "\ntime - " + deviceModel.getTimeStamp() + "\n");
        }

        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);

        //int result = BeaconTransmitter.checkTransmissionSupported(this); // this return device supports to Transmit
        backgroundPowerSaver = new BackgroundPowerSaver(this);


        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Log.d(TAG, "onCreate: devise bluetooth not supported");
        } else if (!mBluetoothAdapter.isEnabled()) {
            Log.d(TAG, "onCreate: bluetooth is disabled");
            setBluetooth(true);
        } else {
            mBluetoothAdapter.isEnabled();
            setupBeacon();
        }


    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Foreground service running")
                .setContentText("This service will run always in the device")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .build();

        startForeground(FOREGROUND_ID, notification);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
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
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                for (Beacon beacon : beacons) {
                    Log.d(TAG, "I see a beacon ");
                    if (beacon.getDistance() < MAX_DISTANCE) {
                        Log.d(TAG, "I see a beacon that is less than 5 meters away.");
                        //deviceRepository.insert(new Device( beacon.getId1().toString(), System.currentTimeMillis()));

                        database_helper.addDevice(beacon.getId1().toString());
                        // Perform distance-specific action here
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
    private void setupBeacon() {
        Beacon beacon = new Beacon.Builder()
                .setId1("7162b38907601ded8f2867a9d45286d5") // need to generate ids device specific
                .setId2("1")
                .setId3("2")
                .setManufacturer(0x0118)
                .setTxPower(-59)
                .setDataFields(Arrays.asList(new Long[]{0l}))
                .build();
        BeaconParser beaconParser = new BeaconParser()
                .setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25");
        BeaconTransmitter beaconTransmitter = new BeaconTransmitter(this, beaconParser);
        beaconTransmitter.startAdvertising(beacon);

        beaconManager = BeaconManager.getInstanceForApplication(this);

        // To detect proprietary beacons, you must add a line like below corresponding to your beacon
        // type.  Do a web search for "setBeaconLayout" to get the proper expression.
        beaconManager.getBeaconParsers().add(beaconParser);
       /* beaconManager.setBackgroundBetweenScanPeriod(0);
        beaconManager.setBackgroundScanPeriod(10000);*/
        beaconManager.bind(this);
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
                        setupBeacon();
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
        if(isInternetAvailable()){
            database_helper.getNotes();

            //database_helper.deleteAlldata();
        }
    }

    @Override
    public void networkUnavailable() {
        Log.d(TAG, "networkUnavailable: ");
    }
    private void setResources(){
        MAX_DISTANCE = ResourcesCompat.getFloat(this.getResources(), R.dimen.max_distance );
        SCAN_PERIOD = this.getResources().getInteger(R.integer.scan_period);
        TIME_BETWEEB_TWO_SCAN = this.getResources().getInteger(R.integer.time_between_scan);
    }
}

