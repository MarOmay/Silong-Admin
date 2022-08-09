package com.silong.admin;

public class PetRecordsData {

    private String genderType;
    private String estAge;
    private String petColor;
    private String estSize;
    private Integer petPic;

    public PetRecordsData(String genderType,  String estAge, String petColor, String estSize, Integer petPic) {
        this.genderType = genderType;
        this.estAge = estAge;
        this.petColor = petColor;
        this.estSize = estSize;
        this.petPic = petPic;
    }

    public String getGenderType() {
        return genderType;
    }

    public void setGenderType(String genderType) {
        this.genderType = genderType;
    }

    public String getEstAge() {
        return estAge;
    }

    public void setEstAge(String estAge) {
        this.estAge = estAge;
    }

    public String getPetColor() {
        return petColor;
    }

    public void setPetColor(String petColor) {
        this.petColor = petColor;
    }

    public String getEstSize() {
        return estSize;
    }

    public void setEstSize(String estSize) {
        this.estSize = estSize;
    }

    public Integer getPetPic() {
        return petPic;
    }

    public void setPetPic(Integer petPic) {
        this.petPic = petPic;
    }
}