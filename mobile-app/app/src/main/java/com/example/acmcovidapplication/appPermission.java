package com.example.acmcovidapplication;

import android.app.job.JobScheduler;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.acmcovidapplication.db.DatabaseHelper;
import com.example.acmcovidapplication.services.CustomService;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class appPermission extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    private static final int PERMISSION_REQUEST_CODE = 1;
    Button allowButton;
    String[] permissions = Util.getPermissions();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_permission);

        allowButton = findViewById(R.id.final_btn);
        stopService( new Intent(this, CustomService.class));
        JobScheduler scheduler = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
            scheduler.cancel(123);
        }


        if (!EasyPermissions.hasPermissions(this, permissions)) {
            DatabaseHelper.getInstance(this).insertAllowed(false);
            EasyPermissions.requestPermissions(this, "This app need Location Access to continue ",
                    PERMISSION_REQUEST_CODE, permissions);
        }


    }

    public void allowAccess(View view) {
        Intent intent = new Intent(appPermission.this, share.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);


        DatabaseHelper.getInstance(this).insertAllowed(true);


    }

    @Override
    protected void onResume() {
        super.onResume();
        allowButton.setEnabled(EasyPermissions.hasPermissions(this, permissions));
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
        String[] permissions = Util.getPermissions();
        allowButton.setEnabled(EasyPermissions.hasPermissions(this, permissions));

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
