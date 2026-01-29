package com.xoraano.deliveryboy.Model;

import java.util.List;

public class OrderRequest {
    private String name,phone,address,pincode,orderId,day_and_date,status,time,dateTime,noOfOrderItems,
            pincode_status,deliveryFee,billReceipt,userNotes,deliveryTimeTaken,date,date_pincode,delivery_rating,food_rating,subTotal,grandTotal,taxRate,payment_method,txnRef;
    private String applied_coupon_code, applied_discount_amount;
    private List<OrderItem> orderItems;
    private MyLocation myLocation;
    private AssignedPerson assignedPerson;
    private String assignedPersonPhone;
    private String razorpay_orderid,razorpayPaymentId;

    public OrderRequest(){

    }

    public void setRazorpayPaymentId(String razorpayPaymentId) {
        this.razorpayPaymentId = razorpayPaymentId;
    }

    public String getRazorpayPaymentId() {
        return razorpayPaymentId;
    }

    public void setRazorpay_orderid(String razorpay_orderid) {
        this.razorpay_orderid = razorpay_orderid;
    }

    public String getRazorpay_orderid() {
        return razorpay_orderid;
    }

    public String getApplied_coupon_code() {
        return applied_coupon_code;
    }

    public String getApplied_discount_amount() {
        return applied_discount_amount;
    }

    public void setApplied_coupon_code(String applied_coupon_code) {
        this.applied_coupon_code = applied_coupon_code;
    }

    public void setApplied_discount_amount(String applied_discount_amount) {
        this.applied_discount_amount = applied_discount_amount;
    }

    public String getTxnRef() {
        return txnRef;
    }

    public void setTxnRef(String txnRef) {
        this.txnRef = txnRef;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

    public String getGrandTotal() {
        return grandTotal;
    }

    public String getSubTotal() {
        return subTotal;
    }

    public String getTaxRate() {
        return taxRate;
    }

    public void setGrandTotal(String grandTotal) {
        this.grandTotal = grandTotal;
    }

    public void setSubTotal(String subTotal) {
        this.subTotal = subTotal;
    }

    public void setTaxRate(String taxRate) {
        this.taxRate = taxRate;
    }

    public String getDelivery_rating() {
        return delivery_rating;
    }

    public String getFood_rating() {
        return food_rating;
    }

    public void setDelivery_rating(String delivery_rating) {
        this.delivery_rating = delivery_rating;
    }

    public void setFood_rating(String food_rating) {
        this.food_rating = food_rating;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDate_pincode(String date_pincode) {
        this.date_pincode = date_pincode;
    }

    public String getDate_pincode() {
        return date_pincode;
    }

    public String getDeliveryTimeTaken() {
        return deliveryTimeTaken;
    }

    public void setDeliveryTimeTaken(String deliveryTimeTaken) {
        this.deliveryTimeTaken = deliveryTimeTaken;
    }

    public String getUserNotes() {
        return userNotes;
    }

    public void setUserNotes(String userNotes) {
        this.userNotes = userNotes;
    }

    public AssignedPerson getAssignedPerson() {
        return assignedPerson;
    }

    public void setMyLocation(MyLocation myLocation) {
        this.myLocation = myLocation;
    }


    public void setBillReceipt(String billReceipt) {
        this.billReceipt = billReceipt;
    }

    public void setDeliveryFee(String deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public String getDeliveryFee() {
        return deliveryFee;
    }


    public void setPincode_status(String pincode_status) {
        this.pincode_status = pincode_status;
    }

    public String getNoOfOrderItems() {
        return noOfOrderItems;
    }

    public void setNoOfOrderItems(String noOfOrderItems) {
        this.noOfOrderItems = noOfOrderItems;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDay_and_date() {
        return day_and_date;
    }

    public void setDay_and_date(String day_and_date) {
        this.day_and_date = day_and_date;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setAssignedPersonPhone(String assignedPersonPhone) {
        this.assignedPersonPhone = assignedPersonPhone;
    }

    public void setAssignedPerson(AssignedPerson assignedPerson) {
        this.assignedPerson = assignedPerson;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getPincode_status() {
        return pincode_status;
    }

    public String getBillReceipt() {
        return billReceipt;
    }

    public MyLocation getMyLocation() {
        return myLocation;
    }

    public String getAssignedPersonPhone() {
        return assignedPersonPhone;
    }
}

