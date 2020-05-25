package com.example.acmcovidapplication.services;


import android.annotation.SuppressLint;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.example.acmcovidapplication.Util;
import com.example.acmcovidapplication.db.DatabaseHelper;
import com.example.acmcovidapplication.db.FirebaseHelper;
import com.example.acmcovidapplication.db.model.DeviceModel;

import java.util.List;

import androidx.annotation.RequiresApi;

import static com.example.acmcovidapplication.services.CustomService.TAG;


@SuppressLint("SpecifyJobSchedulerIdRange")
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CustomJobService extends JobService {

    private boolean jobCancelled = false;
    @Override
    public boolean onStartJob(JobParameters params) {
        doBackgroundWork(params);
        return true;
    }
    private void doBackgroundWork(final JobParameters params) {
        new UploadTask().execute(this); // this will call every fifteen minutes
    }
    @Override
    public boolean onStopJob(JobParameters params) {

        jobCancelled = true;
        return true;
    }

    static class UploadTask extends AsyncTask<Context,Void, Context>{
        List<DeviceModel> list;
        @Override
        protected Context doInBackground(Context... contexts) {
            Context context = contexts[0];
            DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
            list =  databaseHelper.getDevices();
            boolean isInternetConnectionAvailable = Util.isInternetAvailable();
            if(isInternetConnectionAvailable){ return context;}
            return null;
        }

        @Override
        protected void onPostExecute(Context context) {
            super.onPostExecute(context);
            if(context != null){
                DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);

                FirebaseHelper firebaseHelper = new FirebaseHelper();



                if (list != null && list.size()!=0) {
                    Log.d(TAG, "onPostExecute: firebase update method called");
                    for(DeviceModel deviceModel:list){
                        firebaseHelper.update(deviceModel, databaseHelper);
                    }
                }




            }
        }
    }
}