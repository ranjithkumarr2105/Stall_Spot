package com.simats.foodstall.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

public class AdminStall implements Parcelable {

    @SerializedName("stall_id")
    private String stallId;
    @SerializedName("stallname")
    private String stallName;
    @SerializedName("ownername")
    private String ownerName;
    @SerializedName("profile_photo")
    private String profilePhoto;
    @SerializedName("approval")
    private int approval; // -1 for rejected, 0 for pending, 1 for approved
    @SerializedName("rejection_reason")
    private String rejectionReason;

    // --- NEW FIELDS ADDED ---
    @SerializedName("email")
    private String email;
    @SerializedName("phone_number")
    private String phoneNumber;
    @SerializedName("full_address")
    private String fullAddress;
    @SerializedName("fssai_number")
    private String fssaiNumber;
    @SerializedName("date_requested")
    private String dateRequested;
    // --- END OF NEW FIELDS ---


    public String getStallId() { return stallId; }
    public String getStallName() { return stallName; }
    public String getOwnerName() { return ownerName; }
    public String getProfilePhoto() { return profilePhoto; }
    public int getApproval() { return approval; }
    public String getRejectionReason() { return rejectionReason; }

    // --- GETTERS FOR NEW FIELDS ---
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getFullAddress() { return fullAddress; }
    public String getFssaiNumber() { return fssaiNumber; }
    public String getDateRequested() { return dateRequested; }
    // --- END OF NEW GETTERS ---


    // Parcelable implementation updated
    protected AdminStall(Parcel in) {
        stallId = in.readString();
        stallName = in.readString();
        ownerName = in.readString();
        profilePhoto = in.readString();
        approval = in.readInt();
        rejectionReason = in.readString();
        // --- READ NEW FIELDS ---
        email = in.readString();
        phoneNumber = in.readString();
        fullAddress = in.readString();
        fssaiNumber = in.readString();
        dateRequested = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(stallId);
        dest.writeString(stallName);
        dest.writeString(ownerName);
        dest.writeString(profilePhoto);
        dest.writeInt(approval);
        dest.writeString(rejectionReason);
        // --- WRITE NEW FIELDS ---
        dest.writeString(email);
        dest.writeString(phoneNumber);
        dest.writeString(fullAddress);
        dest.writeString(fssaiNumber);
        dest.writeString(dateRequested);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AdminStall> CREATOR = new Creator<AdminStall>() {
        @Override
        public AdminStall createFromParcel(Parcel in) {
            return new AdminStall(in);
        }

        @Override
        public AdminStall[] newArray(int size) {
            return new AdminStall[size];
        }
    };
}