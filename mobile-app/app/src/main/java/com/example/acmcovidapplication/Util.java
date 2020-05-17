package com.example.acmcovidapplication;


import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.SharedPreferences;

import java.net.InetAddress;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

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
    public static boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            //You can replace it with your name
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }
    }
    public SharedPreferences.Editor getSharedPreferenceEditor(String name, Context context){
        SharedPreferences sp = context.getSharedPreferences(name, MODE_PRIVATE);

        return  sp.edit();

    }
}
