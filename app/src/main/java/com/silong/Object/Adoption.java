package com.silong.Object;

import java.io.Serializable;

public class Adoption implements Serializable {

    private int petID;
    private String dateRequested;
    private String appointmentDate;
    private int status;
    private String dateReleased;

    //extra attributes
    private String actualAdoptionDate;
    private String memo;

    public Adoption() {
    }

    public Adoption(int petID, String dateRequested, String appointmentDate, int status, String dateReleased) {
        this.petID = petID;
        this.dateRequested = dateRequested;
        this.appointmentDate = appointmentDate;
        this.status = status;
        this.dateReleased = dateReleased;
    }

    public Adoption(int petID, String appointmentDate, int status) {
        this.petID = petID;
        this.appointmentDate = appointmentDate;
        this.status = status;
    }

    public int getPetID() {
        return petID;
    }

    public void setPetID(int petID) {
        this.petID = petID;
    }

    public String getDateRequested() {
        return dateRequested;
    }

    public void setDateRequested(String dateRequested) {
        this.dateRequested = dateRequested;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDateReleased() {
        return dateReleased;
    }

    public void setDateReleased(String dateReleased) {
        this.dateReleased = dateReleased;
    }

    public String getActualAdoptionDate() {
        return actualAdoptionDate;
    }

    public void setActualAdoptionDate(String actualAdoptionDate) {
        this.actualAdoptionDate = actualAdoptionDate;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
