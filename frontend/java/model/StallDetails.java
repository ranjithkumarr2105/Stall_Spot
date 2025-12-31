package com.simats.foodstall.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class StallDetails implements Serializable {

    @SerializedName("stall_id")
    private String stallId;
    @SerializedName("stallname")
    private String stallName;
    @SerializedName("ownername")
    private String ownerName;
    @SerializedName("phonenumber")
    private String phoneNumber;
    @SerializedName("email")
    private String email;
    @SerializedName("fulladdress")
    private String fullAddress;
    @SerializedName("fssainumber")
    private String fssaiNumber;

    // UPDATED: Added annotation to match the JSON key from the server
    @SerializedName("request_date")
    private String dateRequested;

    // A no-argument constructor is required by libraries like Gson/Retrofit.
    public StallDetails() {}

    // Getters for all fields
    public String getStallId() { return stallId; }
    public String getStallName() { return stallName; }
    public String getOwnerName() { return ownerName; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getEmail() { return email; }
    public String getFullAddress() { return fullAddress; }
    public String getFssaiNumber() { return fssaiNumber; }
    public String getDateRequested() { return dateRequested; }
}