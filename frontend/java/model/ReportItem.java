package com.simats.foodstall.model;

import com.google.gson.annotations.SerializedName;

public class ReportItem {

    @SerializedName("rank")
    private String rank;
    @SerializedName("stallName")
    private String stallName;
    @SerializedName("stallId")
    private String stallId;
    @SerializedName("totalRevenue")
    private double totalRevenue;
    @SerializedName("bestSellingItem")
    private String bestSellingItem;

    // --- Rent details ---
    // These might be null if rent is paid AND acknowledged by admin
    @SerializedName("invoiceId")
    private Integer invoiceId;
    @SerializedName("rentAmount")
    private Double rentAmount;
    @SerializedName("lateFee")
    private Double lateFee;
    @SerializedName("rentStatus")
    private String rentStatus;

    // --- REMOVED isRentHidden flag ---

    // Getters
    public String getRank() { return rank; }
    public String getStallName() { return stallName; }
    public String getStallId() { return stallId; }
    public double getTotalRevenue() { return totalRevenue; }
    public String getBestSellingItem() { return bestSellingItem; }

    // --- Rent Getters ---
    public Integer getInvoiceId() { return invoiceId; }
    public Double getRentAmount() { return rentAmount; }
    public Double getLateFee() { return lateFee; }
    public String getRentStatus() { return rentStatus; }

    // --- REMOVED isRentHidden getter/setter ---

}