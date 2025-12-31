package com.simats.foodstall.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TimeSlotsResponse {

    @SerializedName("status")
    private String status;

    @SerializedName("time_slots")
    private List<String> timeSlots;

    public String getStatus() {
        return status;
    }

    public List<String> getTimeSlots() {
        return timeSlots;
    }
}