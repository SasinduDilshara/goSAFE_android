package com.example.acmcovidapplication.repository;

import android.app.Application;
import android.os.AsyncTask;

import com.example.acmcovidapplication.room_db.Device;
import com.example.acmcovidapplication.room_db.DeviceDao;
import com.example.acmcovidapplication.room_db.DeviceDatabase;

import java.util.List;

import androidx.lifecycle.LiveData;

public class DeviceRepository {
    private DeviceDao deviceDao;
    private LiveData<List<Device>> allNotes;

    public DeviceRepository(Application application) {
        DeviceDatabase database = DeviceDatabase.getInstance(application);
        deviceDao = database.deviceDao();
        allNotes = deviceDao.getAllNotes();
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

    public void deleteAllNotes() {
        new DeleteAllNotesAsyncTask(deviceDao).execute();
    }

    public LiveData<List<Device>> getAllNotes() {
        return allNotes;
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
}