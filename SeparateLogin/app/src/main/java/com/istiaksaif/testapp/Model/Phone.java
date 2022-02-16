package com.istiaksaif.testapp.Model;

public class Phone {
    String phoneNumber,lopen;
    //dealerName,company,state,city,fullAddress,

    public Phone() {
    }

    public Phone(String phoneNumber, String lopen) {
        this.phoneNumber = phoneNumber;
        this.lopen = lopen;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getLopen() {
        return lopen;
    }

    public void setLopen(String lopen) {
        this.lopen = lopen;
    }
}
