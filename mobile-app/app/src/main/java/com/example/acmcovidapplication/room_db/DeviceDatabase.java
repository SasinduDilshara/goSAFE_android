package com.example.acmcovidapplication.room_db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Device.class}, version = 2 , exportSchema = false)
public abstract class DeviceDatabase extends RoomDatabase {


    public abstract DeviceDao deviceDao();

    private static DeviceDatabase instance;

    public static synchronized DeviceDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    DeviceDatabase.class, "device_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            db.execSQL("CREATE TRIGGER validate BEFORE INSERT ON device_table BEGIN SELECT CASE " +
                    "WHEN (CURRENT_TIMESTAMP - (SELECT device_table.time FROM device_table WHERE device_table.id = NEW.id) >  36000)" +
                    " THEN RAISE(ABORT, 'cannot update') \n" +
                    "END;  END");
        }

    };

}
