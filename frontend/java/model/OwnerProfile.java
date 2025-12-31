package com.simats.foodstall.model;

public class OwnerProfile {
    public String stallName;
    public String ownerName;
    public String fullAddress;
    public String phoneNumber;
    public String emailAddress;
    public String password;
    public String fssaiNumber;
    public String ownerId;

    public OwnerProfile(String stallName, String ownerName, String fullAddress, String phoneNumber, String emailAddress, String password, String fssaiNumber, String ownerId) {
        this.stallName = stallName;
        this.ownerName = ownerName;
        this.fullAddress = fullAddress;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
        this.password = password;
        this.fssaiNumber = fssaiNumber;
        this.ownerId = ownerId;
    }
}