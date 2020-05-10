package com.example.acmcovidapplication.broadcast_receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.acmcovidapplication.services.CustomService;

import androidx.core.content.ContextCompat;

public class StartupReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent serviceIntent = new Intent(context, CustomService.class);
        ContextCompat.startForegroundService(context, serviceIntent);

    }
}
