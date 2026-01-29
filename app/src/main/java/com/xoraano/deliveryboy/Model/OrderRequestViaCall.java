package com.xoraano.deliveryboy.Model;

import java.util.List;

public class OrderRequestViaCall {

    private String name,phone,address,pincode,time,grandTotal,day_and_date,dateTime,date,date_pincode,deliveryFee,delivery_rating,food_rating,status,pincode_status,special_notes,orderId,timeCRDelivered,deliveryTimeTaken;
    private List<OrderItemViaCall> orderItems;
    private AssignedPerson assignedPerson;
    private String assignedPersonPhone;

    public void setTimeCRDelivered(String timeCRDelivered) {
        this.timeCRDelivered = timeCRDelivered;
    }

    public String getTimeCRDelivered() {
        return timeCRDelivered;
    }

    public void setDeliveryTimeTaken(String deliveryTimeTaken) {
        this.deliveryTimeTaken = deliveryTimeTaken;
    }

    public String getDeliveryTimeTaken() {
        return deliveryTimeTaken;
    }

    public void setFood_rating(String food_rating) {
        this.food_rating = food_rating;
    }

    public void setDelivery_rating(String delivery_rating) {
        this.delivery_rating = delivery_rating;
    }

    public String getFood_rating() {
        return food_rating;
    }

    public String getDelivery_rating() {
        return delivery_rating;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate_pincode() {
        return date_pincode;
    }

    public String getDate() {
        return date;
    }

    public void setDate_pincode(String date_pincode) {
        this.date_pincode = date_pincode;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<OrderItemViaCall> getOrderItems() {
        return orderItems;
    }

    public String getSpecial_notes() {
        return special_notes;
    }

    public void setSpecial_notes(String special_notes) {
        this.special_notes = special_notes;
    }

    public String getPincode_status() {
        return pincode_status;
    }

    public void setPincode_status(String pincode_status) {
        this.pincode_status = pincode_status;
    }

    public void setOrderItems(List<OrderItemViaCall> orderItems) {
        this.orderItems = orderItems;
    }

    public void setGrandTotal(String grandTotal) {
        this.grandTotal = grandTotal;
    }

    public String getGrandTotal() {
        return grandTotal;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public String getDay_and_date() {
        return day_and_date;
    }

    public String getPhone() {
        return phone;
    }

    public String getDeliveryFee() {
        return deliveryFee;
    }

    public String getPincode() {
        return pincode;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setDay_and_date(String day_and_date) {
        this.day_and_date = day_and_date;
    }

    public void setDeliveryFee(String deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public String getStatus() {
        return status;
    }

    public AssignedPerson getAssignedPerson() {
        return assignedPerson;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAssignedPersonPhone() {
        return assignedPersonPhone;
    }

    public void setAssignedPerson(AssignedPerson assignedPerson) {
        this.assignedPerson = assignedPerson;
    }

    public void setAssignedPersonPhone(String assignedPersonPhone) {
        this.assignedPersonPhone = assignedPersonPhone;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
