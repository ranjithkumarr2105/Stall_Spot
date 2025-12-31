package com.simats.foodstall;

import com.google.gson.annotations.SerializedName;

public class DashboardResponse {

    @SerializedName("status")
    private String status;

    @SerializedName("stall_id")
    private String stallId;

    @SerializedName("orders_today")
    private int ordersToday;

    @SerializedName("revenue_today")
    private double revenueToday;

    @SerializedName("pending_orders")
    private int pendingOrders;

    @SerializedName("top_selling")
    private String topSelling;

    // Add a field for the stall name, which we will fetch separately
    private String stallName;

    // Getters for all fields
    public String getStatus() { return status; }
    public String getStallId() { return stallId; }
    public int getOrdersToday() { return ordersToday; }
    public double getRevenueToday() { return revenueToday; }
    public int getPendingOrders() { return pendingOrders; }
    public String getTopSelling() { return topSelling; }
    public String getStallName() { return stallName; }

    // Setter for stall name
    public void setStallName(String stallName) { this.stallName = stallName; }
}