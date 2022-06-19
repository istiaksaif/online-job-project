package com.istiaksaif.highlymotavated.Model;

import java.io.Serializable;

public class ProductItem implements Serializable {
    private String productImage,userimage,userName,productName,productDescription,reply,productId,
            category,timestamp,productDays,productPrice,Bidders,endTimestamp,userId,sellType;
    int imageCount;

    public ProductItem() {
    }

    public ProductItem(String productImage, String productName, String productDescription, String reply, String productId, String category, String timestamp, String productDays, String productPrice, String bidders, String endTimestamp, String userId,String sellType) {
        this.productImage = productImage;
        this.productName = productName;
        this.productDescription = productDescription;
        this.reply = reply;
        this.productId = productId;
        this.category = category;
        this.timestamp = timestamp;
        this.productDays = productDays;
        this.productPrice = productPrice;
        Bidders = bidders;
        this.endTimestamp = endTimestamp;
        this.userId = userId;
        this.sellType = sellType;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
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

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getProductDays() {
        return productDays;
    }

    public void setProductDays(String productDays) {
        this.productDays = productDays;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getBidders() {
        return Bidders;
    }

    public void setBidders(String bidders) {
        Bidders = bidders;
    }

    public String getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(String endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getImageCount() {
        return imageCount;
    }

    public void setImageCount(int imageCount) {
        this.imageCount = imageCount;
    }

    public String getSellType() {
        return sellType;
    }

    public void setSellType(String sellType) {
        this.sellType = sellType;
    }
}
