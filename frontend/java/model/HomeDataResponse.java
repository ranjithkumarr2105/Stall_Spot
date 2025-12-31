package com.simats.foodstall.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Objects;

public class HomeDataResponse {
    @SerializedName("status") private String status;
    @SerializedName("data") private HomeData data;
    public String getStatus() { return status; }
    public HomeData getData() { return data; }

    public static class HomeData {
        @SerializedName("user_fullname") private String userFullname;
        @SerializedName("specials") private List<SpecialDish> specials;
        @SerializedName("stalls") private List<Stall> stalls;
        @SerializedName("popular_dishes") private List<PopularDish> popularDishes;
        public String getUserFullname() { return userFullname; }
        public List<SpecialDish> getSpecials() { return specials; }
        public List<Stall> getStalls() { return stalls; }
        public List<PopularDish> getPopularDishes() { return popularDishes; }
    }

    // --- SPECIAL DISH MODEL ---
    public static class SpecialDish {
        @SerializedName("stall_id") private String stallId;
        @SerializedName("stall_name") private String stallName;
        @SerializedName("todays_special_name") private String dishName;
        @SerializedName("todays_special_price") private double price;
        @SerializedName("todays_special_image") private String imageUrl;

        // --- NEW CONTENT: Field to check if the stall is open ---
        @SerializedName("isOpen") private int isOpen;

        public String getStallId() { return stallId; }
        public String getStallName() { return stallName; }
        public String getDishName() { return dishName; }
        public double getPrice() { return price; }
        public String getImageUrl() { return imageUrl; }

        // --- NEW CONTENT: Getter for the open status ---
        public boolean isStallOpen() { return isOpen == 1; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SpecialDish that = (SpecialDish) o;
            return Objects.equals(stallId, that.stallId) && Objects.equals(dishName, that.dishName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(stallId, dishName);
        }
    }

    // --- POPULAR DISH MODEL ---
    public static class PopularDish {
        @SerializedName("stall_id") private String stallId;
        @SerializedName("stall_name") private String stallName;
        @SerializedName("item_name") private String dishName;
        @SerializedName("price") private double price;
        @SerializedName("item_image") private String imageUrl;

        // --- NEW CONTENT: Field to check if the stall is open ---
        @SerializedName("isOpen") private int isOpen;

        public String getStallId() { return stallId; }
        public String getStallName() { return stallName; }
        public String getDishName() { return dishName; }
        public double getPrice() { return price; }
        public String getImageUrl() { return imageUrl; }

        // --- NEW CONTENT: Getter for the open status ---
        public boolean isStallOpen() { return isOpen == 1; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PopularDish that = (PopularDish) o;
            return Objects.equals(stallId, that.stallId) && Objects.equals(dishName, that.dishName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(stallId, dishName);
        }
    }
}