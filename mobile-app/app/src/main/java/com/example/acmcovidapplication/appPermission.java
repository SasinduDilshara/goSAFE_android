package com.example.acmcovidapplication;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.acmcovidapplication.db.SharedPeferenceManager;
import com.example.acmcovidapplication.services.CustomService;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class appPermission extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    private static final int PERMISSION_REQUEST_CODE = 1;
    Button allowButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_permission);

        allowButton = findViewById(R.id.final_btn);

        String[] permissions = Util.getPermissions();
        if (!EasyPermissions.hasPermissions(this, permissions)) {
            allowButton.setEnabled(false);
            EasyPermissions.requestPermissions(this, "This app need Location Access to continue ",
                    PERMISSION_REQUEST_CODE, permissions);
        }
    }

    public void allowAccess(View view) {
        Intent intent = new Intent(appPermission.this, share.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        String IS_ALLOWED = this.getResources().getString(R.string.is_allowed);

        SharedPeferenceManager.getSharedPreference(getPackageName(),
                getApplicationContext()).edit().putBoolean(IS_ALLOWED, true).apply();


        Intent serviceIntent = new Intent(this, CustomService.class);

        ContextCompat.startForegroundService(this, serviceIntent);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Util.scheduleJobHelper(this);
        }

    }

    public void goToAbout(View view) {
        Intent intent = new Intent(appPermission.this, about.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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

        allowButton.setEnabled(true);

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

        }


    }
}
