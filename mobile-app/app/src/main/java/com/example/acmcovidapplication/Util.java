package com.example.acmcovidapplication;


import android.Manifest;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import com.example.acmcovidapplication.services.CustomJobService;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;

import androidx.annotation.RequiresApi;

import static android.content.Context.JOB_SCHEDULER_SERVICE;
import static com.example.acmcovidapplication.services.CustomService.TAG;

public class Util {
    public static boolean setBluetooth(boolean enable) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        boolean isEnabled = bluetoothAdapter.isEnabled();
        if (enable && !isEnabled) {
            return bluetoothAdapter.enable();
        } else if (!enable && isEnabled) {
            return bluetoothAdapter.disable();
        }
        // No need to change bluetooth state
        return true;
    }

    public static String[] getPermissions(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION};

            return permissions;
        }
        else {
            String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION};
            return permissions;
        }
    }
    public static boolean isInternetAvailable(Context context) {
        try {
            int timeoutMs = 1500;
            Socket sock = new Socket();
            SocketAddress sockaddr = new InetSocketAddress("8.8.8.8", 53);

            sock.connect(sockaddr, timeoutMs);
            sock.close();

            return true;
        } catch (IOException e) { return false; }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void scheduleJobHelper(Context context){
        ComponentName componentName = new ComponentName(context, CustomJobService.class);
        JobInfo info = null;

        info = new JobInfo.Builder(123, componentName)
                .setPersisted(true)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPeriodic(context.getResources().getInteger(R.integer.upload_period))
                .build();

        JobScheduler scheduler = null;

        scheduler = (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);

        int resultCode = 0;

        resultCode = scheduler.schedule(info);

        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Log.d(TAG, "Job scheduled");
        } else {
            Log.d(TAG, "Job scheduling failed");
        }
    }


    public static String generateHash(String string,Context context){


        String s = new String(Hex.encodeHex(DigestUtils.md5(string + context.getString(R.string.extra_piece)))); // refer string resource file in java_resource.xml
        return s;

    }
}
