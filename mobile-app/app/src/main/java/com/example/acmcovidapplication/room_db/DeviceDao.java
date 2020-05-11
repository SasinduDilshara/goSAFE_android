package com.example.acmcovidapplication.room_db;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface DeviceDao {
    @Insert
    void insert(Device device);
    @Delete
    void delete(Device device);
    @Update
    void update(Device device);
    @Query("DELETE FROM device_table")
    void deleteAll();
    @Query("SELECT * FROM device_table")
    LiveData<List<Device>> getAllNotes();
}
