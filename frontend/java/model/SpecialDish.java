package com.simats.foodstall.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.Objects;

public class SpecialDish implements Serializable {

    @SerializedName("stall_id")
    private String stallId;

    @SerializedName("stall_name")
    private String stallName;

    @SerializedName("todays_special_name")
    private String dishName;

    @SerializedName("todays_special_price")
    private double price;

    @SerializedName("todays_special_image")
    private String imageUrl;

    // Getters
    public String getStallId() { return stallId; }
    public String getStallName() { return stallName; }
    public String getDishName() { return dishName; }
    public double getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }

    // These methods are used by the adapter to compare objects safely
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpecialDish that = (SpecialDish) o;
        return Double.compare(that.price, price) == 0 &&
                Objects.equals(stallId, that.stallId) &&
                Objects.equals(dishName, that.dishName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stallId, dishName, price);
    }
}