package com.simats.foodstall.model;

import com.google.gson.annotations.SerializedName;

public class RazorpayOrderResponse {

    @SerializedName("status")
    private String status;

    @SerializedName("order_id")
    private String orderId;

    @SerializedName("amount")
    private int amountInPaise;

    @SerializedName("key_id")
    private String keyId;

    public String getStatus() { return status; }
    public String getOrderId() { return orderId; }
    public int getAmountInPaise() { return amountInPaise; }
    public String getKeyId() { return keyId; }
}