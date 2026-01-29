package com.xoraano.deliveryboy.Model;

public class OrderItem {
    private String productName,quantity,amount,image;

    public OrderItem(){

    }

    public OrderItem(String productName, String quantity, String amount, String image){
        this.productName = productName;
        this.amount= amount;
        this.quantity=quantity;
        this.image = image;


    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getAmount() {
        return amount;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductName() {
        return productName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
