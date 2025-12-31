package com.simats.foodstall.model;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("role")
    private String role; // Will be "student", "owner_approved", etc.

    @SerializedName("data")
    private UserData data; // A nested object for user details

    // Getters
    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public String getRole() { return role; }
    public UserData getData() { return data; }

    // This nested class now contains ALL possible fields for any user type
    // Removed 'static' to avoid potential issues
    public class UserData {
        @SerializedName("id")
        private int id; // Internal DB ID (from usignup insert)

        @SerializedName("fullname")
        private String fullname;

        @SerializedName("email")
        private String email;

        @SerializedName("student_id")
        private String studentId;

        @SerializedName("phonenumber")
        private String phonenumber; // Can come from owner status check or user profile

        @SerializedName("stall_status")
        private int stallStatus;

        @SerializedName("stall_id")
        private String stallId;

        @SerializedName("stall_name")
        private String stallName;

        @SerializedName("rejection_reason")
        private String rejectionReason;

        // --- NEW: Field for temporary password ---
        @SerializedName("temp_password")
        private String tempPassword;
        // --- End New Field ---


        // Getters for all fields
        public int getId() { return id; }
        public String getFullname() { return fullname; }
        public String getEmail() { return email; }
        public String getStudentId() { return studentId; }
        public String getPhonenumber() { return phonenumber; }
        public int getStallStatus() { return stallStatus; }
        public String getStallId() { return stallId; }
        public String getStallName() { return stallName; }
        public String getRejectionReason() { return rejectionReason; }
        // --- NEW: Getter for temp password ---
        public String getTempPassword() { return tempPassword; }
        // --- End New Getter ---
    }
}