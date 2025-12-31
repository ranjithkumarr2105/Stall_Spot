package com.simats.foodstall.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

public class StallEdit implements Parcelable {

    @SerializedName("stall_id")
    private String stallId;

    @SerializedName("stallname")
    private String stallName;

    @SerializedName("ownername")
    private String ownerName;

    @SerializedName("fssainumber")
    private String fssaiNumber;

    @SerializedName("phonenumber")
    private String contactNumber;

    @SerializedName("latitude")
    private String latitude;

    @SerializedName("longitude")
    private String longitude;

    @SerializedName("profile_photo")
    private String profilePhoto; // URL for the image

    // Getters
    public String getStallId() { return stallId; }
    public String getStallName() { return stallName; }
    public String getOwnerName() { return ownerName; }
    public String getFssaiNumber() { return fssaiNumber; }
    public String getContactNumber() { return contactNumber; }
    public String getLatitude() { return latitude; }
    public String getLongitude() { return longitude; }
    public String getProfilePhoto() { return profilePhoto; }


    // Parcelable Implementation
    protected StallEdit(Parcel in) {
        stallId = in.readString();
        stallName = in.readString();
        ownerName = in.readString();
        fssaiNumber = in.readString();
        contactNumber = in.readString();
        latitude = in.readString();
        longitude = in.readString();
        profilePhoto = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(stallId);
        dest.writeString(stallName);
        dest.writeString(ownerName);
        dest.writeString(fssaiNumber);
        dest.writeString(contactNumber);
        dest.writeString(latitude);
        dest.writeString(longitude);
        dest.writeString(profilePhoto);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<StallEdit> CREATOR = new Creator<StallEdit>() {
        @Override
        public StallEdit createFromParcel(Parcel in) {
            return new StallEdit(in);
        }

        @Override
        public StallEdit[] newArray(int size) {
            return new StallEdit[size];
        }
    };
}