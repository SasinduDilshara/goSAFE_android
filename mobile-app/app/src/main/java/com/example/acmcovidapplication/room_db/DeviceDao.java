package com.example.acmcovidapplication.room_db;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface DeviceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Device device);
    @Delete
    void delete(Device device);
    @Update
    void update(Device device);

    @Query("SELECT EXISTS(SELECT 1 FROM device_table WHERE id=:id LIMIT 1)")
    int isExist(String id);

    @Query("SELECT * FROM device_table WHERE id =:taskId")
    LiveData<Device> getTask(String taskId);
    @Query("DELETE FROM device_table")
    void deleteAll();
    @Query("SELECT * FROM device_table")
    LiveData<List<Device>> getAllNotes();

}
