package com.silong.Object;

public class RequestsData {

    private String requestUserName;
    private String requestDate;
    private Integer requestUserPic;

    public RequestsData(String requestUserName, String requestDate, Integer requestUserPic) {
        this.requestUserName = requestUserName;
        this.requestDate = requestDate;
        this.requestUserPic = requestUserPic;
    }

    public String getRequestUserName() {
        return requestUserName;
    }

    public void setRequestUserName(String requestUserName) {
        this.requestUserName = requestUserName;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String userAccEmail) {
        this.requestDate = requestDate;
    }

    public Integer getRequestUserPic() {
        return requestUserPic;
    }

    public void setRequestUserPic(Integer requestUserPic) {
        this.requestUserPic = requestUserPic;
    }
}
