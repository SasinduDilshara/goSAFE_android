package com.example.acmcovidapplication;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.acmcovidapplication.db.DatabaseHelper;
import com.example.acmcovidapplication.services.CustomService;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import pub.devrel.easypermissions.EasyPermissions;

import static com.example.acmcovidapplication.Util.isMyServiceRunning;

public class share extends AppCompatActivity {
    String[] permissions = Util.getPermissions();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        }
    }

    public void shareApp(View view) {

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        String link = getString(R.string.app_link);
        sendIntent.putExtra(Intent.EXTRA_TEXT, link);
        sendIntent.setType("text/plain");
        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!EasyPermissions.hasPermissions(this, permissions)) {
            DatabaseHelper.getInstance(this).insertAllowed(false);
            stopService(new Intent(this, CustomService.class));
            Intent intent = new Intent(this, appPermission.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        } else if (!isMyServiceRunning(this, CustomService.class)) {
            Intent serviceIntent = new Intent(this, CustomService.class);

            ContextCompat.startForegroundService(this, serviceIntent);
        }


    }

    public void goToAbout(View view) {
        Intent intent = new Intent(share.this, about.class);
        startActivity(intent);
    }

    public void goToPermissionList(View view) {
        Intent intent = new Intent(share.this, permission_list.class);
        startActivity(intent);
    }
}
