package com.example.acmcovidapplication.room_db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Device.class}, version = 1 , exportSchema = false)
public abstract class DeviceDatabase extends RoomDatabase {


    public abstract DeviceDao deviceDao();



}
