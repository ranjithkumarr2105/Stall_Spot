package com.simats.foodstall.model;

import com.google.gson.annotations.SerializedName;

public class RevenueDetail {

    @SerializedName("date")
    private String date;

    @SerializedName("orders")
    private int orders; // Changed from String to int

    @SerializedName("revenue")
    private double revenue; // Changed from String to double

    // Getters for the private fields
    public String getDate() {
        return date;
    }

    public int getOrders() {
        return orders;
    }

    public double getRevenue() {
        return revenue;
    }
}