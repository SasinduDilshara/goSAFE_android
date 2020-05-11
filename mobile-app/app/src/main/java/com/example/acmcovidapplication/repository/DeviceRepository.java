package com.example.acmcovidapplication.repository;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;

import com.example.acmcovidapplication.room_db.Device;
import com.example.acmcovidapplication.room_db.DeviceDao;
import com.example.acmcovidapplication.room_db.DeviceDatabase;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Room;

public class DeviceRepository {
    private static final String DB_NAME = "db_task";
    private DeviceDao deviceDao;
    private LiveData<List<Device>> allNotes;
    private DeviceDatabase deviceDatabase;

    public DeviceRepository(Context context) {
        deviceDatabase = Room.databaseBuilder(context, DeviceDatabase.class, DB_NAME).build();
        deviceDao = deviceDatabase.deviceDao();

    }

    public void insert(Device device) {
        new InsertNoteAsyncTask(deviceDao).execute(device);
    }

    public void update(Device device) {
        new UpdateNoteAsyncTask(deviceDao).execute(device);
    }

    public void delete(Device device) {
        new DeleteNoteAsyncTask(deviceDao).execute(device);
    }

    public void deleteAllDevices() {
        new DeleteAllNotesAsyncTask(deviceDao).execute();
    }

    public LiveData<Device> getDevice(String id) {
        return deviceDatabase.deviceDao().getTask(id);
    }



    public LiveData<List<Device>> getAllDevices() {
        return allNotes = deviceDao.getAllNotes();
    }

    private static class InsertNoteAsyncTask extends AsyncTask<Device, Void, Void> {
        private DeviceDao deviceDao;

        private InsertNoteAsyncTask(DeviceDao deviceDao) {
            this.deviceDao = deviceDao;
        }

        @Override
        protected Void doInBackground(Device... devices) {
            deviceDao.insert(devices[0]);
            return null;
        }
    }

    private static class UpdateNoteAsyncTask extends AsyncTask<Device, Void, Void> {
        private DeviceDao deviceDao;

        private UpdateNoteAsyncTask(DeviceDao deviceDao) {
            this.deviceDao = deviceDao;
        }

        @Override
        protected Void doInBackground(Device... devices) {
            deviceDao.update(devices[0]);
            return null;
        }
    }

    private static class DeleteNoteAsyncTask extends AsyncTask<Device, Void, Void> {
        private DeviceDao deviceDao;

        private DeleteNoteAsyncTask(DeviceDao deviceDao) {
            this.deviceDao = deviceDao;
        }

        @Override
        protected Void doInBackground(Device... devices) {
            deviceDao.delete(devices[0]);
            return null;
        }
    }

    private static class DeleteAllNotesAsyncTask extends AsyncTask<Void, Void, Void> {
        private DeviceDao deviceDao;

        private DeleteAllNotesAsyncTask(DeviceDao deviceDao) {
            this.deviceDao = deviceDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            deviceDao.deleteAll();
            return null;
        }


    }

    private  class CheckExistenceAsync extends AsyncTask<String, Void, Integer> {
        private DeviceDao deviceDao;


        private CheckExistenceAsync(DeviceDao deviceDao) {
            this.deviceDao = deviceDao;


        }


        @Override
        protected Integer doInBackground(String... strings) {

            return deviceDao.isExist(strings[0]);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);

        }
    }

}