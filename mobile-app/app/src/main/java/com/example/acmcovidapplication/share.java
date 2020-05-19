package com.example.acmcovidapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import pub.devrel.easypermissions.EasyPermissions;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.example.acmcovidapplication.services.CustomService;

public class share extends AppCompatActivity {
    String[] permissions = Util.getPermissions();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

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
            if (!EasyPermissions.hasPermissions(this, permissions)) {
                Intent intent = new Intent(this, appPermission.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }
        else{
            Intent serviceIntent = new Intent(this, CustomService.class);

            ContextCompat.startForegroundService(this, serviceIntent);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Util.scheduleJobHelper(this);
            }
        }
    }

    public void goToAbout(View view) {
        Intent intent = new Intent(share.this, about.class);
        startActivity(intent);
    }
}
