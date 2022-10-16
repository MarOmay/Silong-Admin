package com.silong.Object;

import android.graphics.Bitmap;

public class AdminAccountsData {

    String adminAccName;
    String adminAccEmail;
    int adminAvatar;

    public AdminAccountsData(String adminAccName, String adminAccEmail, int adminAvatar) {
        this.adminAccName = adminAccName;
        this.adminAccEmail = adminAccEmail;
        this.adminAvatar = adminAvatar;
    }

    public String getAdminAccName() {
        return adminAccName;
    }

    public void setAdminAccName(String adminAccName) {
        this.adminAccName = adminAccName;
    }

    public String getAdminAccEmail() {
        return adminAccEmail;
    }

    public void setAdminAccEmail(String adminAccEmail) {
        this.adminAccEmail = adminAccEmail;
    }

    public int getAdminAvatar() {
        return adminAvatar;
    }

    public void setAdminAvatar(int adminAvatar) {
        this.adminAvatar = adminAvatar;
    }
}
