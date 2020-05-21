package com.example.acmcovidapplication.broadcast_receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.acmcovidapplication.db.DatabaseHelper;

import static com.example.acmcovidapplication.services.CustomService.TAG;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //Toast.makeText(context,"alrm manager",Toast.LENGTH_SHORT);
        Log.d(TAG, "onReceive: is running");
        DatabaseHelper.getInstance(context).deleteAllTempData();
    }
}
