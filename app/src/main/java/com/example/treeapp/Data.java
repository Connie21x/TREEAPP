package com.example.treeapp;

public class Data {
    private String DataName;
    private String ImageUrl;
    private String CommonName;
    private String FamilyOrigin;
    private String Description;
    private String Uses;
    private String Propagation;
    private String Management;

    public Data(String dataName, String imageUrl, String commonName, String familyOrigin, String description, String uses, String propagation, String management) {
        DataName = dataName;
        ImageUrl = imageUrl;
        CommonName = commonName;
        FamilyOrigin = familyOrigin;
        Description = description;
        Uses = uses;
        Propagation = propagation;
        Management = management;
    }

    public Data() {
    }

    public String getFamilyOrigin() {
        return FamilyOrigin;
    }

    public void setFamilyOrigin(String familyOrigin) {
        FamilyOrigin = familyOrigin;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getUses() {
        return Uses;
    }

    public void setUses(String uses) {
        Uses = uses;
    }

    public String getPropagation() {
        return Propagation;
    }

    public void setPropagation(String propagation) {
        Propagation = propagation;
    }

    public String getManagement() {
        return Management;
    }

    public void setManagement(String management) {
        Management = management;
    }

    public String getCommonName() {
        return CommonName;
    }

    public void setCommonName(String commonName) {
        CommonName = commonName;
    }

    public Data(String commonName) {
        CommonName = commonName;
    }

    public String getDataName() {
        return DataName;
    }

    public void setDataName(String dataName) {
        DataName = dataName;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }
}
