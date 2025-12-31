package com.simats.foodstall.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * This class represents the full JSON response from the server
 * when we ask for a list of stalls.
 */
public class StallDetailsListResponse {

    @SerializedName("status")
    private String status;

    // This tells the app to expect a JSON array named "stalls"
    // and that each item in the array is a StallDetails object.
    @SerializedName("stalls")
    private List<StallDetails> stalls;

    // Getters
    public String getStatus() { return status; }
    public List<StallDetails> getStalls() { return stalls; }
}