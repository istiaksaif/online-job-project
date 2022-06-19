package com.istiaksaif.highlymotavated.Model;
/**
 * Created by Istiak Saif on 28/07/21.
 */

public class User {
    String name, email, phone, dob, isUser, imageUrl, nid, userId, balanceTk, address, key,title,signupdate;

    public User() {
    }

    public User(String name, String email, String phone, String dob, String isUser, String imageUrl, String nid, String userId, String balanceTk, String address, String key, String title,String signupdate) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.dob = dob;
        this.isUser = isUser;
        this.imageUrl = imageUrl;
        this.nid = nid;
        this.userId = userId;
        this.balanceTk = balanceTk;
        this.address = address;
        this.key = key;
        this.title = title;
        this.signupdate = signupdate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getIsUser() {
        return isUser;
    }

    public void setIsUser(String isUser) {
        this.isUser = isUser;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getNid() {
        return nid;
    }

    public void setNid(String nid) {
        this.nid = nid;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBalanceTk() {
        return balanceTk;
    }

    public void setBalanceTk(String balanceTk) {
        this.balanceTk = balanceTk;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSignupdate() {
        return signupdate;
    }

    public void setSignupdate(String signupdate) {
        this.signupdate = signupdate;
    }
}
