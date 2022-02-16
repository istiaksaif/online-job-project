package com.istiaksaif.testapp.Model;

public class ProductItemList {
    private String image,name,price,productId,status;

    public ProductItemList() {
    }

    public ProductItemList(String productId,String image, String name, String price,String status) {
        this.productId = productId;
        this.image = image;
        this.name = name;
        this.price = price;
        this.status = status;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductImage() {
        return image;
    }

    public void setProductImage(String productImage) {
        this.image = productImage;
    }

    public String getProductName() {
        return name;
    }

    public void setProductName(String productName) {
        this.name = productName;
    }

    public String getProductPrice() {
        return price;
    }

    public void setProductPrice(String productPrice) {
        this.price = productPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
