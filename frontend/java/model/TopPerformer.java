package com.simats.foodstall.model;

import com.google.gson.annotations.SerializedName;

public class TopPerformer {
    @SerializedName("item_name")
    private String itemName;
    @SerializedName("total_quantity")
    private int totalQuantity;
    @SerializedName("total_revenue")
    private double totalRevenue;
    @SerializedName("image_url")
    private String imageUrl;

    public String getItemName() { return itemName; }
    public int getTotalQuantity() { return totalQuantity; }
    public double getTotalRevenue() { return totalRevenue; }
    public String getImageUrl() { return imageUrl; }
}