package com.simats.foodstall.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class StallMenuResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private StallMenuData data;

    public String getStatus() { return status; }
    public StallMenuData getData() { return data; }

    public static class StallMenuData {
        @SerializedName("stall_details")
        private StallDetails stallDetails;

        @SerializedName("todays_special")
        private MenuItem todaysSpecial;

        @SerializedName("full_menu")
        private List<MenuItem> fullMenu;

        @SerializedName("popular_dish")
        private MenuItem popularDish;

        @SerializedName("reviews")
        private List<Review> reviews;

        @SerializedName("is_favorite")
        private boolean isFavorite;

        public StallDetails getStallDetails() { return stallDetails; }
        public MenuItem getTodaysSpecial() { return todaysSpecial; }
        public List<MenuItem> getFullMenu() { return fullMenu; }
        public MenuItem getPopularDish() { return popularDish; }
        public List<Review> getReviews() { return reviews; }
        public boolean isFavorite() { return isFavorite; }
    }

    public static class StallDetails {
        @SerializedName("stallname")
        private String stallName;

        // --- START OF UPDATED SECTION ---
        @SerializedName("latitude")
        private double latitude;

        @SerializedName("longitude")
        private double longitude;
        // --- END OF UPDATED SECTION ---

        public String getStallName() { return stallName; }

        // --- START OF UPDATED SECTION ---
        public double getLatitude() { return latitude; }
        public double getLongitude() { return longitude; }
        // --- END OF UPDATED SECTION ---
    }
}