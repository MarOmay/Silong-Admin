package com.silong.Object;

import android.graphics.Bitmap;

public class Pet {

    private String petID;
    private int status;
    private int type;
    private int gender;
    private String color;
    private int age;
    private int size;
    private Bitmap photo;
    private String photoAsString;
    private String modifiedBy;
    private String lastModified;
    private int likes;

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
        this.likes = likes;
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

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }
}
