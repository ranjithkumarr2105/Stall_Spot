package com.simats.foodstall.model;

import com.google.gson.annotations.SerializedName;

public class AStallLocation {
    @SerializedName("stall_id")
    private String stallId;

    @SerializedName("stallname")
    private String stallName;

    @SerializedName("latitude")
    private double latitude;

    @SerializedName("longitude")
    private double longitude;

    public String getStallId() {
        return stallId;
    }

    public String getStallName() {
        return stallName;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}