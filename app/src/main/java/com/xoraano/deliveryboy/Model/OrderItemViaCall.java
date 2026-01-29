package com.xoraano.deliveryboy.Model;

public class OrderItemViaCall {

    private String name, price, quantity, amount;

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

    public String getAmount() {
        return amount;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
