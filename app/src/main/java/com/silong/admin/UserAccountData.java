package com.silong.admin;

public class UserAccountData {

    private String userAccName;
    private String userAccEmail;
    private Integer userAccPic;

    public UserAccountData(String userAccName, String userAccEmail, Integer userAccPic) {
        this.userAccName = userAccName;
        this.userAccEmail = userAccEmail;
        this.userAccPic = userAccPic;
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
}

