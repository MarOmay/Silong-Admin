package com.silong.Object;

public class LogData {

    private String date;
    private String time;
    private String description;
    private String email;
    private String deviceMaker;
    private String deviceModel;

    public LogData(){

    }

    public LogData(String logRecordDate, String logRecordDesc){
        this.date = logRecordDate;
        this.description = logRecordDesc;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDeviceMaker() {
        return deviceMaker;
    }

    public void setDeviceMaker(String deviceMaker) {
        this.deviceMaker = deviceMaker;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }
}
