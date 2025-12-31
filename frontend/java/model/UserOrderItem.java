package com.simats.foodstall.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

public class UserOrderItem implements Parcelable {
    @SerializedName("name")
    private String name;
    @SerializedName("quantity")
    private int quantity;
    @SerializedName("price")
    private double price;

    // --- THIS IS THE FINAL FIX ---
    // Changed "parcelStatus" to "parcel_status" to match the JSON key from the server.
    @SerializedName("parcel_status")
    private String parcelStatus;
    // --- END OF FIX ---

    public String getName() { return name; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public String getParcelStatus() { return parcelStatus; }

    // Parcelable Implementation (This was already correct and is unchanged)
    protected UserOrderItem(Parcel in) {
        name = in.readString();
        quantity = in.readInt();
        price = in.readDouble();
        parcelStatus = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(quantity);
        dest.writeDouble(price);
        dest.writeString(parcelStatus);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserOrderItem> CREATOR = new Creator<UserOrderItem>() {
        @Override
        public UserOrderItem createFromParcel(Parcel in) {
            return new UserOrderItem(in);
        }

        @Override
        public UserOrderItem[] newArray(int size) {
            return new UserOrderItem[size];
        }
    };
}