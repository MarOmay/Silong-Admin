package com.silong.Object;

public class LogData {

    String logRecordDate;
    String logRecordDesc;

    public LogData(String logRecordDate, String logRecordDesc){
        this.logRecordDate = logRecordDate;
        this.logRecordDesc = logRecordDesc;
    }

    public String getLogRecordDate() {
        return logRecordDate;
    }

    public void setLogRecordDate(String logRecordDate) {
        this.logRecordDate = logRecordDate;
    }

    public String getLogRecordDesc() {
        return logRecordDesc;
    }

    public void setLogRecordDesc(String logRecordDesc) {
        this.logRecordDesc = logRecordDesc;
    }
}
