package com.xoraano.deliveryboy.Model;

public class DeliveryPerson {
    private String name,image,mobileNo,password,pincode,is_blocked,fcm_token;
    private MyLocation my_location;

    public DeliveryPerson(String name,String image,String mobileNo,String password,String pincode){

        this.name=name;
        this.image=image;
        this.mobileNo=mobileNo;
        this.password=password;
        this.pincode=pincode;
    }

    public DeliveryPerson(){

    }

    public String getFcm_token() {
        return fcm_token;
    }

    public void setFcm_token(String fcm_token) {
        this.fcm_token = fcm_token;
    }

    public String getIs_blocked() {
        return is_blocked;
    }

    public void setMy_location(MyLocation my_location) {
        this.my_location = my_location;
    }

    public MyLocation getMy_location() {
        return my_location;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getPincode() {
        return pincode;
    }

    public String getImage() {
        return image;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
