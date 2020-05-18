package com.example.acmcovidapplication.workers;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import static com.example.acmcovidapplication.services.CustomService.TAG;

public class CustomWorker extends Worker {

    public CustomWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
    }

    @Override
    public Result doWork() {
        // Do the work here--in this case, upload the images.
        Log.d(TAG, "doWork: in periodic" );

        // Indicate whether the task finished successfully with the Result
        return Result.success();
    }


}
