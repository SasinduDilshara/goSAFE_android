package com.example.acmcovidapplication.services;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.example.acmcovidapplication.R;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.BeaconTransmitter;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

import java.util.Collection;
import java.util.Collections;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import static com.example.acmcovidapplication.App.CHANNEL_ID;
import static com.example.acmcovidapplication.services.CustomService.BEACON_LAYOUT;
import static com.example.acmcovidapplication.services.CustomService.TAG;

public class TestService extends Service implements BootstrapNotifier {

    private BeaconManager beaconManager;
    private Beacon beacon;
    private RegionBootstrap regionBootstrap;
    private BackgroundPowerSaver backgroundPowerSaver;


    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "onCreate: is called");



    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStart: is called");
        BeaconParser beaconParser = new BeaconParser()
                .setBeaconLayout(BEACON_LAYOUT);

        beacon = new Beacon.Builder()
                .setId1("4102757c-9c29-11ea-bb37-0242ac130002") // need to generate ids device specific
                .setId2("1")
                .setId3("2")
                .setManufacturer(0x0118)
                .setTxPower(-59)
                .setDataFields(Collections.singletonList(0L))
                .build();

        BeaconTransmitter beaconTransmitter = new BeaconTransmitter(this, beaconParser);
        beaconTransmitter.startAdvertising(beacon);
        Region region = new Region("com.example.myapp.boostrapRegion", null, null, null);

        BeaconManager beaconManager = org.altbeacon.beacon.BeaconManager.getInstanceForApplication(this);

            regionBootstrap = new RegionBootstrap(this, region);
            backgroundPowerSaver = new BackgroundPowerSaver(this);
       /* beaconManager.setBackgroundBetweenScanPeriod(0);
        beaconManager.setBackgroundScanPeriod(10000);*/
            beaconManager.getBeaconParsers().clear();
            beaconManager.getBeaconParsers().add(beaconParser);



        beaconManager.setBackgroundScanPeriod(10000);
        beaconManager.setBackgroundBetweenScanPeriod(5000);
        beaconManager.setRegionStatePersistenceEnabled(true);
        BeaconManager.setAndroidLScanningDisabled(false);
        BeaconManager.setRegionExitPeriod(1000);
        BeaconManager.setUseTrackingCache(true);
        beaconManager.applySettings();


        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(this.getResources().getString(R.string.app_name) + " is active")
                .setContentText("Keeping this app running will  save you from  \nbecoming a COVID-19 victim")
                .setSmallIcon(R.mipmap.app_icon)
                .build();

        startForeground(1, notification);
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void didEnterRegion(Region region) {
        Log.d(TAG, "didEnterRegion: found");
    }

    @Override
    public void didExitRegion(Region region) {
        Log.d(TAG, "didExitRegion: exit");
    }

    @Override
    public void didDetermineStateForRegion(int i, Region region) {

    }
}
