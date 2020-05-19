package com.example.acmcovidapplication.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import static com.example.acmcovidapplication.services.CustomService.TAG;


public class DatabaseHelper extends SQLiteOpenHelper {

    //database name
    private static final String DATABASE_NAME  = "final";
    //database version
    private static final int DATABASE_VERSION  = 20;
    private static final String TABLE_NAME     = "final_tb";

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    private static DatabaseHelper  databaseHelper = null;
    public static DatabaseHelper getInstance(Context context){
        if(databaseHelper == null){
            synchronized (DatabaseHelper.class){
                if (databaseHelper == null){
                    databaseHelper = new DatabaseHelper(context);
                }
            }
        }

        return databaseHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query;
        //creating table
        query = "CREATE TABLE " + TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, USERID TEXT, TIMESTAMP DATETIME DEFAULT CURRENT_TIMESTAMP)";
        db.execSQL(query);
        db.execSQL("DROP TRIGGER IF EXISTS validate");
        db.execSQL(" CREATE TRIGGER  validate BEFORE INSERT ON " + TABLE_NAME +
                " FOR EACH ROW BEGIN SELECT CASE WHEN (SELECT COUNT(userid) " +
                "FROM " + TABLE_NAME + " WHERE userid = NEW.userid AND (strftime('%s', CURRENT_TIMESTAMP) -  Strftime('%s', timestamp ))/60 < 60) > 0" +
                " THEN RAISE(ABORT, 'cannot update') END; END");

    }

    //upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

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
            sqLiteDatabase.insert(TABLE_NAME, null , values);
        }
        catch (Exception e)
        {
            Log.d(TAG, "addNotes: " +e.getMessage());
        }

        //close database connection
        sqLiteDatabase.close();
    }

    //get the all notes
    public ArrayList<DeviceModel> getNotes() {
        ArrayList<DeviceModel> arrayList = new ArrayList<>();

        // select all query
        String select_query= "SELECT *FROM " + TABLE_NAME;

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
        return arrayList;
    }

    //delete the note
    public void delete(int ID) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        //deleting row
        sqLiteDatabase.delete(TABLE_NAME, "ID=" + ID, null);
        sqLiteDatabase.close();
    }

    //update the note
    public void updateNote(String title, String des, String ID) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues values =  new ContentValues();
        values.put("Title", title);
        values.put("Description", des);
        //updating row
        sqLiteDatabase.update(TABLE_NAME, values, "ID=" + ID, null);
        sqLiteDatabase.close();
    }

    public void deleteAlldata(){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL("DELETE FROM " + TABLE_NAME);
    }


}