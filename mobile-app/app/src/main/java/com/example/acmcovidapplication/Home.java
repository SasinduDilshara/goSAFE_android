package com.example.acmcovidapplication;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.example.acmcovidapplication.services.CustomService;

import java.util.List;

public class Home extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private static final int PERMISSION_REQUEST_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        String[] permissions = Util.getPermissions();
        if (!EasyPermissions.hasPermissions(this, permissions)) {

            EasyPermissions.requestPermissions(this, "We need permissions to continue", PERMISSION_REQUEST_CODE, permissions);
        }
        else{
            Intent serviceIntent = new Intent(this, CustomService.class);

            ContextCompat.startForegroundService(this, serviceIntent);
        }
    }

    public void goToLogin(View view) {
        Intent intent = new Intent(Home.this, login.class);
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
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            finish();
            stopService(new Intent(this, CustomService.class));
        }



    }
}
