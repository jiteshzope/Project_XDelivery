package com.xoraano.deliveryboy.Model;

public class MyLocation {
    private Double latitude,longitude;

    public MyLocation(){

    }
    public MyLocation(Double latitude, Double longitude){
        this.latitude=latitude;
        this.longitude=longitude;

    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
