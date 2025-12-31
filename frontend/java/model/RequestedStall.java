package com.simats.foodstall.model;

public class RequestedStall {
    private int stallImageResId;
    private String stallName;
    private String ownerName;
    private String emailAddress; // New field
    private String requestedDate;

    public RequestedStall(int stallImageResId, String stallName, String ownerName, String emailAddress, String requestedDate) {
        this.stallImageResId = stallImageResId;
        this.stallName = stallName;
        this.ownerName = ownerName;
        this.emailAddress = emailAddress; // Updated constructor
        this.requestedDate = requestedDate;
    }

    // Getters
    public int getStallImageResId() { return stallImageResId; }
    public String getStallName() { return stallName; }
    public String getOwnerName() { return ownerName; }
    public String getEmailAddress() { return emailAddress; } // New getter
    public String getRequestedDate() { return requestedDate; }
}