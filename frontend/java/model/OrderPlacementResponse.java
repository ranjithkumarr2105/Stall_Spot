package com.simats.foodstall.model;
import com.google.gson.annotations.SerializedName;

public class OrderPlacementResponse {
    @SerializedName("status") private String status;
    @SerializedName("message") private String message;

    // UPDATED: Changed from int orderId to String displayOrderId
    @SerializedName("display_order_id")
    private String displayOrderId;

    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public String getDisplayOrderId() { return displayOrderId; } // Getter for the new field
}