package com.example.acmcovidapplication.broadcast_receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.example.acmcovidapplication.Util;
import com.example.acmcovidapplication.db.DatabaseHelper;

import static com.example.acmcovidapplication.services.CustomService.TAG;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //Toast.makeText(context,"alrm manager",Toast.LENGTH_SHORT);
        Log.d(TAG, "onReceive: is running");

            Log.d(TAG, "onReceive: internet is available");
            new CheckInternetAndDeleteAsync().execute(context);

    }

    private static class CheckInternetAndDeleteAsync extends AsyncTask<Context, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Context... contexts) {
            if(Util.isInternetAvailable()){
                DatabaseHelper.getInstance(contexts[0]).deleteAllTempData();
            }
            return true;
        }


    }
}
