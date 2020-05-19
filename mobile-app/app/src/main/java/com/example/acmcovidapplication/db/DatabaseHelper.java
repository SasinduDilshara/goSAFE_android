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

import java.util.ArrayList;

import static com.example.acmcovidapplication.services.CustomService.TAG;


public class DatabaseHelper extends SQLiteOpenHelper {

    //database name
    private static    String DATABASE_NAME ;
    //database version
    private static  int DATABASE_VERSION;
    private static    String USER_LOG_TABLE_NAME;
    private static String APP_DATA_TABLE_NAME;

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    private static DatabaseHelper  databaseHelper = null;
    public static DatabaseHelper getInstance(Context context){
        if(databaseHelper == null){
            synchronized (DatabaseHelper.class){
                if (databaseHelper == null){
                    Resources resource = context.getResources();

                    DATABASE_NAME = resource.getString(R.string.database_name);
                    DATABASE_VERSION = resource.getInteger(R.integer.database_version);
                    USER_LOG_TABLE_NAME = resource.getString(R.string.user_log_table_name);
                    APP_DATA_TABLE_NAME = resource.getString(R.string.app_data_table_name);

                    databaseHelper = new DatabaseHelper(context);
                }
            }
        }

        return databaseHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query_user_log,query_app_data;
        //creating table
        query_user_log = "CREATE TABLE " + USER_LOG_TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, USERID TEXT, TIMESTAMP DATETIME DEFAULT CURRENT_TIMESTAMP)";
        query_app_data = "CREATE TABLE " + APP_DATA_TABLE_NAME + "(ID INTEGER PRIMARY KEY , USER_ID TEXT TYPE UNIQUE, IS_ALLOWED INTEGER  )";


        db.execSQL(query_user_log);
        db.execSQL(query_app_data);

        db.execSQL("INSERT INTO " + APP_DATA_TABLE_NAME + " (ID) " + " VALUES (1)");

        db.execSQL("DROP TRIGGER IF EXISTS validate");
        db.execSQL(" CREATE TRIGGER  validate BEFORE INSERT ON " + USER_LOG_TABLE_NAME +
                " FOR EACH ROW BEGIN SELECT CASE WHEN (SELECT COUNT(userid) " +
                "FROM " + USER_LOG_TABLE_NAME + " WHERE userid = NEW.userid AND (strftime('%s', CURRENT_TIMESTAMP) -  Strftime('%s', timestamp ))/60 < 60) > 0" +
                " THEN RAISE(ABORT, 'cannot update') END; END");

    }

    //upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + USER_LOG_TABLE_NAME);

        onCreate(db);
    }

    //add the new note
    public void addDevice(String userId) {
        SQLiteDatabase sqLiteDatabase = this .getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("USERID", userId);


        //inserting new row
        long newRowId;

        try {
            sqLiteDatabase.insert(USER_LOG_TABLE_NAME, null , values);
        }
        catch (Exception e)
        {
            Log.d(TAG, "addNotes: " +e.getMessage());
        }

        //close database connection
        sqLiteDatabase.close();
    }

    //get the all notes
    public ArrayList<DeviceModel> getDevices() {
        ArrayList<DeviceModel> arrayList = new ArrayList<>();

        // select all query
        String select_query= "SELECT * FROM " + USER_LOG_TABLE_NAME;

        SQLiteDatabase db = this .getWritableDatabase();
        Cursor cursor = db.rawQuery(select_query, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                DeviceModel deviceModel = new DeviceModel();
                deviceModel.setID(cursor.getInt(cursor.getColumnIndex("ID")));
                deviceModel.setUserID(cursor.getString(1));
                deviceModel.setTimeStamp(cursor.getString(2));
                arrayList.add(deviceModel);
            }while (cursor.moveToNext());
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
        ContentValues values =  new ContentValues();
        values.put("Title", title);
        values.put("Description", des);
        //updating row
        sqLiteDatabase.update(USER_LOG_TABLE_NAME, values, "ID=" + ID, null);
        sqLiteDatabase.close();
    }

    public void deleteAllDevice(){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL("DELETE FROM " + USER_LOG_TABLE_NAME);
    }

    public void insertAllowed(boolean allowed){
        int i =0;
        if(allowed){ i = 1;}
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues newValues = new ContentValues();
        newValues.put("IS_ALLOWED", i);

        sqLiteDatabase.update(APP_DATA_TABLE_NAME, newValues, "ID=1", null);
    }

    public void insertUserId(String  user_id){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues newValues = new ContentValues();
        newValues.put("USER_ID", user_id);

        sqLiteDatabase.update(APP_DATA_TABLE_NAME, newValues, "ID=1", null);

    }

    public String getUserId(){
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + APP_DATA_TABLE_NAME + " WHERE  ID = 1",null);
        cursor.moveToFirst();

        int column = cursor.getColumnIndex("USER_ID");
        String userId = cursor.getString(cursor.getColumnIndex("USER_ID"));
        cursor.close();
        return userId;


    }

    public boolean getAllowed(){
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + APP_DATA_TABLE_NAME + " WHERE  ID = 1" ,null);
        cursor.moveToFirst();

        int isAllowed = cursor.getInt(cursor.getColumnIndex("IS_ALLOWED"));
        cursor.close();
        return isAllowed == 1;
    }

    private static class DeleteDeviceAsync extends AsyncTask<Integer,Void,Void>{
        DatabaseHelper database;

        public DeleteDeviceAsync(DatabaseHelper database){
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
}