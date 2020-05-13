package com.example.acmcovidapplication.room_db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "device_table")
public class Device {

    @PrimaryKey
    @NonNull
    private String id;
    private long time;

    public Device(String id, long time) {
        this.id = id;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public long getTime() {
        return time;
    }
}
