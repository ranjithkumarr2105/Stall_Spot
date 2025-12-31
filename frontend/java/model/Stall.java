package com.simats.foodstall.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Stall implements Serializable {

    @SerializedName("stall_id")
    private String stallId;

    @SerializedName("stallname")
    private String stallName;

    @SerializedName("rating")
    private double rating;

    @SerializedName("isOpen")
    private int openStatus;

    @SerializedName("profile_photo")
    private String profilePhotoUrl;

    @SerializedName("isFavorite")
    private int isFavorite;

    // --- START OF NEW SECTION ---
    @SerializedName("opening_hours")
    private String openingHours;

    @SerializedName("closing_hours")
    private String closingHours;
    // --- END OF NEW SECTION ---

    public String getStallId() { return stallId; }
    public String getStallName() { return stallName; }
    public double getRating() { return rating; }
    public boolean isOpen() { return openStatus == 1; }
    public String getProfilePhotoUrl() { return profilePhotoUrl; }
    public boolean isFavorite() { return isFavorite == 1; }

    // --- START OF NEW SECTION ---
    public String getOpeningHours() { return openingHours; }
    public String getClosingHours() { return closingHours; }
    // --- END OF NEW SECTION ---
}