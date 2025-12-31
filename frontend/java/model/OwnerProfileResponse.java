package com.simats.foodstall.model;

import com.google.gson.annotations.SerializedName;

public class OwnerProfileResponse {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private OwnerData data;

    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public OwnerData getData() { return data; }

    // "static" keyword has been removed from this class declaration
    public class OwnerData {
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

        @SerializedName("stall_id")
        private String stallId;

        @SerializedName("profile_photo")
        private String profilePhoto;

        public String getStallName() { return stallName; }
        public String getOwnerName() { return ownerName; }
        public String getPhoneNumber() { return phoneNumber; }
        public String getEmail() { return email; }
        public String getFullAddress() { return fullAddress; }
        public String getFssaiNumber() { return fssaiNumber; }
        public String getStallId() { return stallId; }
        public String getProfilePhoto() { return profilePhoto; }
    }
}