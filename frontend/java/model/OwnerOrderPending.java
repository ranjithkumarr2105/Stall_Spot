package com.simats.foodstall.model;

public class OwnerOrderPending {
    // You can add more fields like studentId, orderType, parcelFee, etc.
    private String orderId;
    private String stallName;
    private String stallLocation;
    private String orderItems;
    private String totalPrice;
    private String timeAgo;
    private String orderTime;

    public OwnerOrderPending(String orderId, String stallName, String stallLocation, String orderItems, String totalPrice, String timeAgo, String orderTime) {
        this.orderId = orderId;
        this.stallName = stallName;
        this.stallLocation = stallLocation;
        this.orderItems = orderItems;
        this.totalPrice = totalPrice;
        this.timeAgo = timeAgo;
        this.orderTime = orderTime;
    }

    // --- Getters for all fields ---
    public String getOrderId() { return orderId; }
    public String getStallName() { return stallName; }
    public String getStallLocation() { return stallLocation; }
    public String getOrderItems() { return orderItems; }
    public String getTotalPrice() { return totalPrice; }
    public String getTimeAgo() { return timeAgo; }
    public String getOrderTime() { return orderTime; }
}