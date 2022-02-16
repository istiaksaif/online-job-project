package com.istiaksaif.testapp.Model;

public class DealerItem {
    String dealerName,company,dealerId,state,city,address,NewCompaniesId,limit,status;

    public DealerItem() {
    }

    public DealerItem(String dealerName, String company, String dealerId, String state, String city,
                      String address,String NewCompaniesId,String limit,String status) {
        this.dealerName = dealerName;
        this.company = company;
        this.dealerId = dealerId;
        this.state = state;
        this.city = city;
        this.address = address;
        this.NewCompaniesId = NewCompaniesId;
        this.limit = limit;
        this.status = status;
    }

    public String getDealerName() {
        return dealerName;
    }

    public void setDealerName(String dealerName) {
        this.dealerName = dealerName;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getDealerId() {
        return dealerId;
    }

    public void setDealerId(String dealerId) {
        this.dealerId = dealerId;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNewCompaniesId() {
        return NewCompaniesId;
    }

    public void setNewCompaniesId(String newCompaniesId) {
        NewCompaniesId = newCompaniesId;
    }

    public String getLimit() {
        return limit;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
