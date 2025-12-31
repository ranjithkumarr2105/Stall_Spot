package com.simats.foodstall.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class StallLocationResponse {

    @SerializedName("status")
    private String status;

    @SerializedName("locations")
    private List<AStallLocation> locations;

    public String getStatus() {
        return status;
    }

    public List<AStallLocation> getLocations() {
        return locations;
    }
}