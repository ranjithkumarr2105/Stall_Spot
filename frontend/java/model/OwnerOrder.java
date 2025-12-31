package com.simats.foodstall.model;

public class OwnerOrder {
    public String orderId;
    public String stallName;
    public String userId;
    public String itemsCount;
    public String totalAmount;
    public String pickupTime;

    public OwnerOrder(String orderId, String stallName, String userId, String itemsCount, String totalAmount, String pickupTime) {
        this.orderId = orderId;
        this.stallName = stallName;

        this.itemsCount = itemsCount;
        this.totalAmount = totalAmount;
        this.pickupTime = pickupTime;
    }
}