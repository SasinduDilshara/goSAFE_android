package com.example.acmcovidapplication;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;

import com.example.acmcovidapplication.services.CustomService;

import androidx.core.content.ContextCompat;


public class App extends Application {
    public static final String CHANNEL_ID = "com.example.acmcovidapplication.ServiceChannel";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();

        Intent serviceIntent = new Intent(this, CustomService.class);

        ContextCompat.startForegroundService(this, serviceIntent);

    }



    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
}