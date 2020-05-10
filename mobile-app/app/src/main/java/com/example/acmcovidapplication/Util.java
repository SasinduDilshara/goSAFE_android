package com.example.acmcovidapplication;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;

public class Util {

    public static final int PERMISSION_REQUEST_FINE_LOCATION = 1;
    public static final int PERMISSION_REQUEST_BACKGROUND_LOCATION = 2;
    public static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;


    public static void requestPermission(final Activity context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check
            if (context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("This app needs location access");
                builder.setMessage("Please grant location access so this app can detect beacons.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {


                    public void onDismiss(DialogInterface dialog) {
                        context.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                    }
                });
                builder.show();
            }
        }
    }
}
