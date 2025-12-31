package com.simats.foodstall.model;

import com.google.gson.annotations.SerializedName;

/**
 * A generic response model for API calls that primarily return a status and a message.
 * This class replaces the duplicate RegisterResponse files.
 */
public class StatusResponse {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    // This field is optional and is used when approving a stall
    @SerializedName("stall_id")
    private String stallId;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    // Getter for the optional stallId
    public String getStallId() {
        return stallId;
    }
}