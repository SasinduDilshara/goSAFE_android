package com.example.acmcovidapplication.room_db;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Device.class}, version = 1)
public abstract class DeviceDatabase extends RoomDatabase {
    private static DeviceDatabase instance;

    public abstract DeviceDao deviceDao();

    public static DeviceDatabase getInstance(Context context){
        if (instance == null){
            synchronized (DeviceDatabase.class){
                if (instance == null){
                    instance = Room.databaseBuilder(context.getApplicationContext(),DeviceDatabase.class,"device_database")
                                    .fallbackToDestructiveMigration()
                                    .addCallback(roomCallback)
                                    .build();

                }
            }
        }

        return instance;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
        private DeviceDao noteDao;

        private PopulateDbAsyncTask(DeviceDatabase db) {
            noteDao = db.deviceDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            noteDao.insert(new Device("sample1", "time_sample 1"));
            noteDao.insert(new Device("sample2", "time_sample 2"));
            return null;
        }
    }
}
