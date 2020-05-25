package com.example.acmcovidapplication.broadcast_receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.example.acmcovidapplication.Util;
import com.example.acmcovidapplication.db.DatabaseHelper;
import com.example.acmcovidapplication.services.CustomService;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import static com.example.acmcovidapplication.services.CustomService.TAG;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //Toast.makeText(context,"alrm manager",Toast.LENGTH_SHORT);
        Log.d(TAG, "onReceive: is running");
        Intent intent1 = new Intent(CustomService.ACTION_KEEP_TRANSMITTER_ALIVE);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent1);

        new CheckInternetAndDeleteAsync().execute(context);


    }

    private static class CheckInternetAndDeleteAsync extends AsyncTask<Context, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Context... contexts) {

            if (Util.isInternetAvailable()) {
                Log.d(TAG, "onReceive: internet is available");
                DatabaseHelper.getInstance(contexts[0]).deleteAllTempData();
            }
            return true;
        }


    }
}
