package com.istiaksaif.highlymotavated.Model;

public class CartItemModel {
    private String cartId,productId,name,price,itemtotalprice,quantity,image,totalPrice,status,trackId,Id;

    public CartItemModel() {

    }

    public CartItemModel(String cartId, String productId, String name, String price, String itemtotalprice, String quantity, String image, String status, String trackId) {
        this.cartId = cartId;
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.itemtotalprice = itemtotalprice;
        this.quantity = quantity;
        this.image = image;
        this.status = status;
        this.trackId = trackId;
    }

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getItemtotalprice() {
        return itemtotalprice;
    }

    public void setItemtotalprice(String itemtotalprice) {
        this.itemtotalprice = itemtotalprice;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }
}
