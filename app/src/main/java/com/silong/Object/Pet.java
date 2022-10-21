package com.silong.Object;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class Pet {

    private String petID;
    private int status;
    private int type;
    private int gender;
    private String color;
    private int age;
    private int size;
    private Bitmap photo;
    private ArrayList<String> extraPhotosAsString = new ArrayList<>();
    private String photoAsString;
    private String modifiedBy;
    private String lastModified;
    private String owner;
    private String rescueDate;
    private String distMark;

    public Pet() {
    }

    public Pet(String id, int status, int type, int gender, String color, int age, int size, int likes) {
        this.petID = id;
        this.status = status;
        this.type = type;
        this.gender = gender;
        this.color = color;
        this.age = age;
        this.size = size;
    }

    public String getPetID() {
        return petID;
    }

    public void setPetID(String petID) {
        this.petID = petID;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    public ArrayList<String> getExtraPhotosAsString() {
        return extraPhotosAsString;
    }

    public void setExtraPhotosAsString(ArrayList<String> extraPhotosAsString) {
        this.extraPhotosAsString = extraPhotosAsString;
    }

    public String getPhotoAsString() {
        return photoAsString;
    }

    public void setPhotoAsString(String photoAsString) {
        this.photoAsString = photoAsString;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getRescueDate() {
        return rescueDate;
    }

    public void setRescueDate(String rescueDate) {
        this.rescueDate = rescueDate;
    }

    public String getDistMark() {
        return distMark;
    }

    public void setDistMark(String distMark) {
        this.distMark = distMark;
    }
}
