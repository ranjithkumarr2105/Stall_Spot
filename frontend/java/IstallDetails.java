package com.simats.foodstall;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class IstallDetails implements Serializable {

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
    @SerializedName("latitude")
    private double latitude;
    @SerializedName("longitude")
    private double longitude;
    @SerializedName("approval")
    private int approvalStatus;

    // --- V V V NEW FIELD ADDED V V V ---
    @SerializedName("request_date")
    private String requestDate;
    // --- ^ ^ ^ END OF NEW FIELD ^ ^ ^ ---

    // Getters for all fields
    public String getStallId() { return stallId; }
    public String getStallName() { return stallName; }
    public String getOwnerName() { return ownerName; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getEmail() { return email; }
    public String getFullAddress() { return fullAddress; }
    public String getFssaiNumber() { return fssaiNumber; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public int getApprovalStatus() { return approvalStatus; }

    // --- V V V NEW GETTER ADDED V V V ---
    public String getRequestDate() { return requestDate; }
    // --- ^ ^ ^ END OF NEW GETTER ^ ^ ^ ---
}