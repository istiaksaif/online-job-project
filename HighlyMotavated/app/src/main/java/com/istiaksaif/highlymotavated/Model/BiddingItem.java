package com.istiaksaif.highlymotavated.Model;

public class BiddingItem {
    private String productimage,userimage,userName,productname,productdes,reply,productId,
            category,datetime,productdays,productprice,biders,enddate,userId,biddingId;

    public BiddingItem() {
    }

    public BiddingItem(String userimage, String userName, String datetime, String productprice, String userId,String biddingId) {
        this.userimage = userimage;
        this.userName = userName;
        this.datetime = datetime;
        this.productprice = productprice;
        this.userId = userId;
        this.biddingId = biddingId;
    }

    public String getUserimage() {
        return userimage;
    }

    public void setUserimage(String userimage) {
        this.userimage = userimage;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getProductprice() {
        return productprice;
    }

    public void setProductprice(String productprice) {
        this.productprice = productprice;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBiddingId() {
        return biddingId;
    }

    public void setBiddingId(String biddingId) {
        this.biddingId = biddingId;
    }
}
