package com.istiaksaif.highlymotavated.Model;

public class NotifyItem {
    private String title,message,notifyId,productId,datetime,userId;

    public NotifyItem() {
    }

    public NotifyItem(String title, String message, String notifyId, String productId, String datetime, String userId) {
        this.title = title;
        this.message = message;
        this.notifyId = notifyId;
        this.productId = productId;
        this.datetime = datetime;
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNotifyId() {
        return notifyId;
    }

    public void setNotifyId(String notifyId) {
        this.notifyId = notifyId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
