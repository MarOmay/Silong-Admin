package com.silong.Object;

import android.graphics.Bitmap;

public class UserAccountData {

    private String userID;
    private String userAccName;
    private String userAccEmail;
    private Integer userAccPic;
    private Bitmap userAvatar;

    public UserAccountData(String userAccName, String userAccEmail, Integer userAccPic) {
        this.userAccName = userAccName;
        this.userAccEmail = userAccEmail;
        this.userAccPic = userAccPic;
    }

    public UserAccountData(String uid, String userAccName, String userAccEmail, Bitmap userAvatar) {
        this.userID = uid;
        this.userAccName = userAccName;
        this.userAccEmail = userAccEmail;
        this.userAvatar = userAvatar;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserAccName() {
        return userAccName;
    }

    public void setUserAccName(String userAccName) {
        this.userAccName = userAccName;
    }

    public String getUserAccEmail() {
        return userAccEmail;
    }

    public void setUserAccEmail(String userAccEmail) {
        this.userAccEmail = userAccEmail;
    }

    public Integer getUserAccPic() {
        return userAccPic;
    }

    public void setUserAccPic(Integer userAccPic) {
        this.userAccPic = userAccPic;
    }

    public Bitmap getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(Bitmap userAvatar) {
        this.userAvatar = userAvatar;
    }
}

