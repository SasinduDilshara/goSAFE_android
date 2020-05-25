package com.example.acmcovidapplication.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

import com.example.acmcovidapplication.R;
import com.example.acmcovidapplication.db.model.DeviceModel;

import java.util.ArrayList;

import static com.example.acmcovidapplication.services.CustomService.TAG;


public class DatabaseHelper extends SQLiteOpenHelper {

    //database name
    private static String DATABASE_NAME;
    //database version
    private static int DATABASE_VERSION;
    private static String USER_LOG_TABLE_NAME;
    private static String APP_DATA_TABLE_NAME;
    private static int update_time;
    private static String TEMPORARY_LOG_TABLE_NAME;

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    private static DatabaseHelper databaseHelper = null;

    public static DatabaseHelper getInstance(Context context) {
        if (databaseHelper == null) {
            synchronized (DatabaseHelper.class) {
                if (databaseHelper == null) {
                    Resources resource = context.getResources();

                    DATABASE_NAME = resource.getString(R.string.database_name);
                    DATABASE_VERSION = resource.getInteger(R.integer.database_version);
                    USER_LOG_TABLE_NAME = resource.getString(R.string.user_log_table_name);
                    APP_DATA_TABLE_NAME = resource.getString(R.string.app_data_table_name);
                    TEMPORARY_LOG_TABLE_NAME = resource.getString(R.string.temp_log_table_name);
                    update_time = resource.getInteger(R.integer.update_period);

                    databaseHelper = new DatabaseHelper(context);
                }
            }
        }

        return databaseHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query_user_log, query_app_data, query_temp_log;
        //creating table
        query_user_log = "CREATE TABLE " + USER_LOG_TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, USERID TEXT," +
                " TIMESTAMP_UP DATETIME DEFAULT CURRENT_TIMESTAMP," +
                " LATITUDE DOUBLE, LONGITUDE DOUBLE )";
        query_app_data = "CREATE TABLE " + APP_DATA_TABLE_NAME + "(ID INTEGER PRIMARY KEY , USER_ID TEXT TYPE UNIQUE, IS_ALLOWED INTEGER  )";
        query_temp_log = "CREATE TABLE " + TEMPORARY_LOG_TABLE_NAME +
                "( USER_ID TEXT PRIMARY KEY, TIMESTAMP_UP INTEGER )";


        db.execSQL(query_user_log);
        db.execSQL(query_app_data);
        db.execSQL(query_temp_log);

        db.execSQL("INSERT INTO " + APP_DATA_TABLE_NAME + " (ID) " + " VALUES (1)");

        //db.execSQL("DROP TRIGGER IF EXISTS validate");
        /*db.execSQL(" CREATE TRIGGER  validate BEFORE INSERT ON " + USER_LOG_TABLE_NAME +
                " FOR EACH ROW BEGIN SELECT CASE WHEN (SELECT COUNT(userid) " +
                "FROM " + USER_LOG_TABLE_NAME + " WHERE userid = NEW.userid AND  ((julianday(CURRENT_TIMESTAMP) - julianday(timestamp_up)) * 86400.0)/60 < "+60+") > 0" +
                " THEN RAISE(ABORT, 'cannot update') END; END");*/

    }

    //upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + USER_LOG_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + APP_DATA_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TEMPORARY_LOG_TABLE_NAME);

        onCreate(db);
    }

    //add the new note
    public void addDevice(String userId,double latitude,double longitude) {

        String select_query = "SELECT * FROM " + TEMPORARY_LOG_TABLE_NAME + " WHERE USER_ID = '" + userId + "'";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(select_query, null);

        if (cursor.moveToFirst()) {
                long result = cursor.getInt(1);
                long now = (long)System.currentTimeMillis()/10000;
                long difference = now -result;

                if (difference> update_time * 6) {

                    DeviceModel deviceModel = new DeviceModel();
                    deviceModel.setUserID(userId);
                    if(latitude != 0 && longitude != 0) {
                        deviceModel.setLatitude(latitude);
                        deviceModel.setLongitude(longitude);
                    }
                    new InsertDeviceAsync(this).execute(deviceModel);
                    ContentValues cv = new ContentValues();
                    cv.put("USER_ID", userId);
                    cv.put("TIMESTAMP_UP",(long)(System.currentTimeMillis()/10000));
                    db.update(TEMPORARY_LOG_TABLE_NAME, cv, " USER_ID = '" + userId + "'", null);
                    Log.d(TAG, "addDevice: log table updated");

                } else {

                     Log.d(TAG, "addDevice: no need to insert");
                }



        } else {
            ContentValues cv = new ContentValues();
            cv.put("USER_ID", userId);
            cv.put("TIMESTAMP_UP",(long)(System.currentTimeMillis()/10000));
            Log.d(TAG, "addDevice: added to log table");
            db.insert(TEMPORARY_LOG_TABLE_NAME, null, cv);
            DeviceModel deviceModel= new DeviceModel();
            deviceModel.setUserID(userId);
            if(latitude != 0 && longitude != 0) {
                deviceModel.setLatitude(latitude);
                deviceModel.setLongitude(longitude);
            }
            new InsertDeviceAsync(this).execute(deviceModel);

        }

        db.close();
        cursor.close();


    }

    //get the all notes
    public ArrayList<DeviceModel> getDevices() {
        ArrayList<DeviceModel> arrayList = new ArrayList<>();

        // select all query
        String select_query = "SELECT * FROM " + USER_LOG_TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(select_query, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                DeviceModel deviceModel = new DeviceModel();
                deviceModel.setID(cursor.getInt(cursor.getColumnIndex("ID")));
                deviceModel.setUserID(cursor.getString(1));
                deviceModel.setTimeStamp(cursor.getString(2));
                deviceModel.setLatitude(cursor.getDouble(cursor.getColumnIndex("LATITUDE")) );
                deviceModel.setLongitude(cursor.getDouble(cursor.getColumnIndex("LONGITUDE")));
                arrayList.add(deviceModel);
            } while (cursor.moveToNext());
        }
        db.close();
        cursor.close();
        return arrayList;
    }

    //delete the note
    public void deleteDevice(int ID) {
        new DeleteDeviceAsync(this).execute(ID);
    }


    //update the note
    public void updateDevice(String title, String des, String ID) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Title", title);
        values.put("Description", des);
        //updating row
        sqLiteDatabase.update(USER_LOG_TABLE_NAME, values, "ID=" + ID, null);
        sqLiteDatabase.close();
    }

    public void deleteAllDevice() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL("DELETE FROM " + USER_LOG_TABLE_NAME);
        Log.d(TAG, "deleteAllDevice: called from alarm manager");
    }

    public void deleteAllTempData(){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL("DELETE FROM " + TEMPORARY_LOG_TABLE_NAME);
        Log.d(TAG, "deleteAllTempData: called from by alarm manager");
    }

    public void insertAllowed(boolean allowed) {
        int i = 0;
        if (allowed) {
            i = 1;
        }
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues newValues = new ContentValues();
        newValues.put("IS_ALLOWED", i);

        sqLiteDatabase.update(APP_DATA_TABLE_NAME, newValues, "ID=1", null);
    }

    public void insertUserId(String user_id) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues newValues = new ContentValues();
        newValues.put("USER_ID", user_id);

        sqLiteDatabase.update(APP_DATA_TABLE_NAME, newValues, "ID=1", null);


    }

    public String getUserId() {
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + APP_DATA_TABLE_NAME + " WHERE  ID = 1", null);
        cursor.moveToFirst();

        int column = cursor.getColumnIndex("USER_ID");
        String userId = cursor.getString(cursor.getColumnIndex("USER_ID"));
        cursor.close();
        return userId;


    }

    public boolean getAllowed() {
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + APP_DATA_TABLE_NAME + " WHERE  ID = 1", null);
        cursor.moveToFirst();

        int isAllowed = cursor.getInt(cursor.getColumnIndex("IS_ALLOWED"));
        cursor.close();
        return isAllowed == 1;
    }

    private static class DeleteDeviceAsync extends AsyncTask<Integer, Void, Void> {
        DatabaseHelper database;

        public DeleteDeviceAsync(DatabaseHelper database) {
            this.database = database;
        }

        @Override
        protected Void doInBackground(Integer... integers) {

            SQLiteDatabase sqLiteDatabase = database.getWritableDatabase();
            //deleting row
            sqLiteDatabase.delete(USER_LOG_TABLE_NAME, "ID = " + integers[0], null);
            sqLiteDatabase.close();
            return null;
        }
    }

    private static class InsertDeviceAsync extends AsyncTask<DeviceModel, Void, Void> {
        DatabaseHelper database;

        public InsertDeviceAsync(DatabaseHelper database) {
            this.database = database;
        }

        @Override
        protected Void doInBackground(DeviceModel... deviceModels) {

            SQLiteDatabase sqLiteDatabase = database.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("USERID", deviceModels[0].getUserID());

            values.put("LATITUDE",deviceModels[0].getLatitude());
            values.put("LONGITUDE",deviceModels[0].getLongitude());




            //inserting new row
            long newRowId;

            try {
                long success = sqLiteDatabase.insert(USER_LOG_TABLE_NAME, null, values);

                Log.e(TAG, "doInBackground: database inserted is " + success);
            } catch (Exception e) {
                Log.d(TAG, "addNotes: " + e.getMessage());
            }

            //close database connection

            sqLiteDatabase.close();

            return null;
        }
    }


}