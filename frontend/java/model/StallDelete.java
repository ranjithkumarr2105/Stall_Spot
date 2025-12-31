package com.simats.foodstall.model;

public class StallDelete {
    public String stallName;
    public String ownerId;
    public String ownerName;
    public String latitude;
    public String longitude;
    public boolean isClosed;
    public String closeReason;

    public StallDelete(String stallName, String ownerId, String ownerName, String latitude, String longitude, boolean isClosed, String closeReason) {
        this.stallName = stallName;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isClosed = isClosed;
        this.closeReason = closeReason;
    }
}