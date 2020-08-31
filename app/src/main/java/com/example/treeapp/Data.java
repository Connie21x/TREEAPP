package com.example.treeapp;

public class Data {
    private String DataName;
    private String ImageUrl;
    private String CommonName;

    public Data(String dataName, String imageUrl, String commonName) {
        DataName = dataName;
        ImageUrl = imageUrl;
        CommonName = commonName;
    }

    public Data() {
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
