package com.example.acmcovidapplication.db;

import androidx.annotation.NonNull;

public class DeviceModel {

    int ID;
    String UserID;
    String timeStamp;

    public int getID() {
        return ID;
    }

    public String getUserID() {
        return UserID;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    @NonNull
    @Override
    public String toString() {
        return "id - " + ID + String.format("\nisAllowed -%s", UserID) + "\nthis device id - "+ timeStamp;
    }
}