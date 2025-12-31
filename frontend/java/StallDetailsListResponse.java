package com.simats.foodstall;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class StallDetailsListResponse {

    @SerializedName("status")
    private String status;

    // Updated to use a List of IstallDetails
    @SerializedName("stalls")
    private List<IstallDetails> stalls;

    // Getters
    public String getStatus() { return status; }
    public List<IstallDetails> getStalls() { return stalls; }
}