package com.simats.foodstall.model;

import com.google.gson.annotations.SerializedName;

public class UserProfileResponse {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private UserData data;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public UserData getData() {
        return data;
    }

    // "static" keyword has been removed from this class declaration
    public class UserData {
        @SerializedName("fullname")
        private String fullname;

        @SerializedName("email")
        private String email;

        @SerializedName("phonenumber")
        private String phonenumber;

        @SerializedName("student_id")
        private String studentId;

        @SerializedName("profile_photo")
        private String profilePhoto;

        public String getFullname() {
            return fullname;
        }

        public String getEmail() {
            return email;
        }

        public String getPhonenumber() {
            return phonenumber;
        }

        public String getStudentId() {
            return studentId;
        }

        public String getProfilePhoto() {
            return profilePhoto;
        }
    }
}