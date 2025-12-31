package com.simats.foodstall.model;

public class OrderStatus {
    private String stallName;
    private String dateTime;
    private String orderId;
    private String items;
    private double price;
    private String status;

    public OrderStatus(String stallName, String dateTime, String orderId, String items, double price, String status) {
        this.stallName = stallName;
        this.dateTime = dateTime;
        this.orderId = orderId;
        this.items = items;
        this.price = price;
        this.status = status;
    }

    public String getStallName() {
        return stallName;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getItems() {
        return items;
    }

    public double getPrice() {
        return price;
    }

    public String getStatus() {
        return status;
    }
}