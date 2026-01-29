package com.xoraano.deliveryboy.Model;

public class CartItem {
    private String productName,image,quantity,amount;

    public CartItem(String productName, String image, String quantity, String amount){
        this.productName = productName;
        this.image = image;
        this.quantity = quantity;
        this.amount = amount;
    }

    public CartItem(){

    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

}

