package com.simats.foodstall.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class PopularDish implements Serializable {
    @SerializedName("stall_id") private String stallId;
    @SerializedName("stall_name") private String stallName;
    @SerializedName("item_name") private String dishName;
    @SerializedName("price") private double price;
    @SerializedName("item_image") private String imageUrl;
    public String getStallId() { return stallId; }
    public String getStallName() { return stallName; }
    public String getDishName() { return dishName; }
    public double getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
}