package com.example.acmcovidapplication;


import android.Manifest;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import com.example.acmcovidapplication.services.CustomJobService;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import androidx.annotation.RequiresApi;

import static android.content.Context.JOB_SCHEDULER_SERVICE;
import static android.content.Context.MODE_PRIVATE;
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
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            try{
                HttpURLConnection connection = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                connection.setRequestProperty("User-Agent", "Test");
                connection.setRequestProperty("Connection", "close");
                connection.setConnectTimeout(3000); //choose your own timeframe
                connection.setReadTimeout(4000); //choose your own timeframe
                connection.connect();
                int responseCode = connection.getResponseCode();
                if (responseCode == 200) { //Connection OK
                    return true;
                } else {
                    return  false;
                }
            }catch (Exception e){
                return  false; //connectivity exists, but no internet.
            }
        } else {
            return  false; //no connectivity
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    static void scheduleJobHelper(Context context){
        ComponentName componentName = new ComponentName(context, CustomJobService.class);
        JobInfo info = null;

        info = new JobInfo.Builder(123, componentName)
                .setPersisted(true)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPeriodic(context.getResources().getInteger(R.integer.scan_period))
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
