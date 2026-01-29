package com.xoraano.deliveryboy.Model;

public class AssignedPerson {
    private String personName,personImage,personMobile;

    public AssignedPerson(){

    }


    public String getPersonImage() {
        return personImage;
    }

    public String getPersonMobile() {
        return personMobile;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonImage(String personImage) {
        this.personImage = personImage;
    }

    public void setPersonMobile(String personMobile) {
        this.personMobile = personMobile;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

}
