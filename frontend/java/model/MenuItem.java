package com.simats.foodstall.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.Objects;

public class MenuItem implements Serializable {
    @SerializedName("name") private String name;
    @SerializedName("price") private double price;
    @SerializedName("image") private String imageUrl;
    @SerializedName("sold_count") private int soldCount; // For popular dish card

    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
    public int getSoldCount() { return soldCount; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MenuItem menuItem = (MenuItem) o;
        return Double.compare(menuItem.price, price) == 0 &&
                soldCount == menuItem.soldCount &&
                Objects.equals(name, menuItem.name) &&
                Objects.equals(imageUrl, menuItem.imageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, price, imageUrl, soldCount);
    }
}