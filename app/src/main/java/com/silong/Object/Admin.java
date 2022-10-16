package com.silong.Object;

import java.io.Serializable;

public class Admin implements Serializable {

    private String adminID;
    private String adminEmail;
    private String firstName;
    private String lastName;
    private String contact;
    private boolean accountStatus;
    private String [] userInteraction; //userid of users

    private boolean role_manageRequests = false;
    private boolean role_appointments = false;
    private boolean role_manageRecords = false;
    private boolean role_manageReports = false;
    private boolean role_editAgreement = false;
    private boolean role_editContact = false;
    private boolean role_editSchedule = false;
    private boolean role_manageRoles = false;

    public Admin(){}

    public Admin(String adminID, String adminEmail, String firstName, String lastName, boolean accountStatus, String[] userInteraction) {
        this.adminID = adminID;
        this.adminEmail = adminEmail;
        this.firstName = firstName;
        this.lastName = lastName;
        this.accountStatus = accountStatus;
        this.userInteraction = userInteraction;
    }

    public String getAdminID() {
        return adminID;
    }

    public void setAdminID(String adminID) {
        this.adminID = adminID;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public boolean isAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(boolean accountStatus) {
        this.accountStatus = accountStatus;
    }

    public String[] getUserInteraction() {
        return userInteraction;
    }

    public void setUserInteraction(String[] userInteraction) {
        this.userInteraction = userInteraction;
    }

    public boolean isRole_manageRequests() {
        return role_manageRequests;
    }

    public void setRole_manageRequests(boolean role_manageRequests) {
        this.role_manageRequests = role_manageRequests;
    }

    public boolean isRole_appointments() {
        return role_appointments;
    }

    public void setRole_appointments(boolean role_appointments) {
        this.role_appointments = role_appointments;
    }

    public boolean isRole_manageRecords() {
        return role_manageRecords;
    }

    public void setRole_manageRecords(boolean role_manageRecords) {
        this.role_manageRecords = role_manageRecords;
    }

    public boolean isRole_manageReports() {
        return role_manageReports;
    }

    public void setRole_manageReports(boolean role_manageReports) {
        this.role_manageReports = role_manageReports;
    }

    public boolean isRole_editAgreement() {
        return role_editAgreement;
    }

    public void setRole_editAgreement(boolean role_editAgreement) {
        this.role_editAgreement = role_editAgreement;
    }

    public boolean isRole_editContact() {
        return role_editContact;
    }

    public void setRole_editContact(boolean role_editContact) {
        this.role_editContact = role_editContact;
    }

    public boolean isRole_editSchedule() {
        return role_editSchedule;
    }

    public void setRole_editSchedule(boolean role_editSchedule) {
        this.role_editSchedule = role_editSchedule;
    }

    public boolean isRole_manageRoles() {
        return role_manageRoles;
    }

    public void setRole_manageRoles(boolean role_manageRoles) {
        this.role_manageRoles = role_manageRoles;
    }
}
