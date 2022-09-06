package com.silong.Object;

import android.graphics.Bitmap;

public class AppointmentRecords {

    private String name;
    private String dateTime;
    private String petId;
    private Bitmap userPic;

    private String userID;

    public AppointmentRecords(String name, String dateTime, String petId, Bitmap userPic, String userID) {
        this.name = name;
        this.dateTime = dateTime;
        this.petId = petId;
        this.userPic = userPic;
        this.userID = userID;
    }

    public AppointmentRecords() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getPetId() {
        return petId;
    }

    public void setPetId(String petId) {
        this.petId = petId;
    }

    public Bitmap getUserPic() {
        return userPic;
    }

    public void setUserPic(Bitmap userPic) {
        this.userPic = userPic;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
