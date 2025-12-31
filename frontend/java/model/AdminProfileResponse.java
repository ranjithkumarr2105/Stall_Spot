package com.simats.foodstall.model;

import com.google.gson.annotations.SerializedName;

public class AdminProfileResponse {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private AdminData data;

    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public AdminData getData() { return data; }

    // "static" keyword has been removed from this class declaration
    public class AdminData {
        @SerializedName("fullname")
        private String fullname;

        @SerializedName("email")
        private String email;

        @SerializedName("phonenumber")
        private String phonenumber;

        @SerializedName("profile_photo")
        private String profilePhoto;

        public String getFullname() { return fullname; }
        public String getEmail() { return email; }
        public String getPhonenumber() { return phonenumber; }
        public String getProfilePhoto() { return profilePhoto; }
    }
}