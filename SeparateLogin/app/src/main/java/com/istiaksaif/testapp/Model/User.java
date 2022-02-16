package com.istiaksaif.testapp.Model;

public class User {
    String name,email,designation,company,division,mobile,state,city,image,userId,token,userType,
            adarcard,status;

    public User() {
    }

    public User(String name, String email, String designation, String company, String division,
                String mobile, String state, String city, String image, String userId,
                String token,String userType,String adarcard,String status) {
        this.name = name;
        this.email = email;
        this.designation = designation;
        this.company = company;
        this.division = division;
        this.mobile = mobile;
        this.state = state;
        this.city = city;
        this.image = image;
        this.userId = userId;
        this.token = token;
        this.userType = userType;
        this.adarcard = adarcard;
        this.status = status;
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

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getAdarcard() {
        return adarcard;
    }

    public void setAdarcard(String adarcard) {
        this.adarcard = adarcard;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
