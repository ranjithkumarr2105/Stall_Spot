package com.simats.foodstall.model;

import com.google.gson.annotations.SerializedName;

public class RegisterResponse {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    // ADDED: Field to receive the new stall_id upon approval
    @SerializedName("stall_id")
    private String stallId;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    // ADDED: Getter method for the new field. This will fix the error in your activity.
    public String getStallId() {
        return stallId;
    }
}