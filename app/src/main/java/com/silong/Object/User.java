package com.silong.Object;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {

    public String userID;
    public String email;
    public String firstName;
    public String lastName;
    public String birthday;
    public int gender;
    public String contact;
    public Bitmap photo;
    public String photoAsString;
    public String lastModified;
    public boolean accountStatus;
    public int adoptionCounter;
    public com.silong.Object.Address address;
    public ArrayList<Adoption> adoptionHistory = new ArrayList<>();

    private boolean isDeleted;

    public User() {
    }

    public String getUserID() {
        return userID;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    public String getPhotoAsString() {
        return photoAsString;
    }

    public void setPhotoAsString(String photoAsString) {
        this.photoAsString = photoAsString;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public boolean getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(boolean accountStatus) {
        this.accountStatus = accountStatus;
    }

    public int getAdoptionCounter() {
        return adoptionCounter;
    }

    public void setAdoptionCounter(int adoptionCounter) {
        this.adoptionCounter = adoptionCounter;
    }

    public com.silong.Object.Address getAddress() {
        return address;
    }

    public void setAddress(com.silong.Object.Address address) {
        this.address = address;
    }

    public ArrayList<Adoption> getAdoptionHistory() {
        return adoptionHistory;
    }

    public void setAdoptionHistory(ArrayList<Adoption> adoptionHistory) {
        this.adoptionHistory = adoptionHistory;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}
