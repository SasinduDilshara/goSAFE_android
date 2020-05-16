package com.example.acmcovidapplication.db;

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
}