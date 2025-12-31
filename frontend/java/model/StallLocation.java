package com.simats.foodstall.model;

import org.osmdroid.util.GeoPoint;

public class StallLocation {

    // --- UPDATED PART START ---
    private String stallId; // Added the stallId field
    // --- UPDATED PART END ---
    private String name;
    private GeoPoint location;

    // --- UPDATED PART START ---
    // Constructor updated to include the stallId
    public StallLocation(String stallId, String name, GeoPoint location) {
        this.stallId = stallId;
        this.name = name;
        this.location = location;
    }
    // --- UPDATED PART END ---

    // --- UPDATED PART START ---
    // New getter for the stallId
    public String getStallId() {
        return stallId;
    }
    // --- UPDATED PART END ---

    public String getName() {
        return name;
    }

    public GeoPoint getLocation() {
        return location;
    }
}