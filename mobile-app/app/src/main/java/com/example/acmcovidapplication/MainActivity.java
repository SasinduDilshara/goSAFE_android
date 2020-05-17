package com.example.acmcovidapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.view.WindowManager;


import android.Manifest;

import android.os.Build;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import com.example.acmcovidapplication.db.DatabaseHelper;
import com.example.acmcovidapplication.db.DeviceModel;
import com.example.acmcovidapplication.services.CustomService;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;


public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private static final String TAG = "MainActivity";
    private static final int PERMISSION_REQUEST_CODE = 1;

    private static int SPLASH_SCREEN_TIME_OUT=2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //This method is used so that your splash activity
        //can cover the entire screen.

        setContentView(R.layout.activity_main);

        //this will bind your MainActivity.class file with activity_main.

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
//            Toast.makeText(getApplicationContext(),FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().toString(),Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(MainActivity.this, appPermission.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                }
                else{
                    Intent i=new Intent(MainActivity.this,
                            home.class);
                    //Intent is used to switch from one activity to another.

                    startActivity(i);
                    //invoke the SecondActivity.
                }

                finish();
                //the current activity will get finished.
            }
        }, SPLASH_SCREEN_TIME_OUT);


        DatabaseHelper database_helper = new DatabaseHelper(this);
        for (DeviceModel deviceModel: database_helper.getNotes()){
            Log.d(TAG, "onCreate: user id- " + deviceModel.getUserID() + "\n" +
                    "time - " + deviceModel.getTimeStamp());
        }
        String[] permissions = Util.getPermissions();
        if (!EasyPermissions.hasPermissions(this, permissions)) {

            EasyPermissions.requestPermissions(this, "We need permissions to continue", PERMISSION_REQUEST_CODE, permissions);
        }


    }


    public void goToLogin(View view) {
        Intent intent = new Intent(MainActivity.this, login.class);
        startActivity(intent);

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

        Intent serviceIntent = new Intent(this, CustomService.class);

        ContextCompat.startForegroundService(this, serviceIntent);
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {

            new AppSettingsDialog.Builder(this).build().show();

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            finish();
            stopService(new Intent(this, CustomService.class));
        }




    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
////            Toast.makeText(getApplicationContext(),FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().toString(),Toast.LENGTH_LONG).show();
//            Intent intent = new Intent(this, appPermission.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(intent);
//
//        }
//    }


}

