package com.simats.foodstall.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

/**
 * Represents a single favorite stall fetched from the server.
 */
public class FavoriteStall implements Serializable {

    @SerializedName("stall_id")
    private String stallId;

    @SerializedName("stallname")
    private String name;


    @SerializedName("profile_photo")
    private String imageUrl;

    @SerializedName("rating")
    private double rating;

    // Getters for all fields
    public String getStallId() { return stallId; }
    public String getName() { return name; }
    public String getImageUrl() { return imageUrl; }
    public double getRating() { return rating; }
}