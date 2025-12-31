package com.simats.foodstall.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import java.util.Objects;

public class OrderItem implements Parcelable {

    @SerializedName("name")
    private String name;

    @SerializedName("quantity")
    private int quantity;

    @SerializedName("price")
    private double price;

    @SerializedName("parcel_status")
    private String parcelStatus;

    public OrderItem() {}

    public OrderItem(String name, int quantity, double price, String parcelStatus) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.parcelStatus = parcelStatus;
    }

    // Getters
    public String getName() { return name; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public String getParcelStatus() { return parcelStatus; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setPrice(double price) { this.price = price; }
    public void setParcelStatus(String parcelStatus) { this.parcelStatus = parcelStatus; }

    // --- METHODS RESTORED TO FIX THE CRASH ---
    public void incrementQuantity() {
        this.quantity++;
    }

    public void decrementQuantity() {
        if (this.quantity > 0) {
            this.quantity--;
        }
    }
    // ------------------------------------------

    // Parcelable Implementation (Updated to include the new field)
    protected OrderItem(Parcel in) {
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
    public int describeContents() { return 0; }

    public static final Creator<OrderItem> CREATOR = new Creator<OrderItem>() {
        @Override
        public OrderItem createFromParcel(Parcel in) {
            return new OrderItem(in);
        }

        @Override
        public OrderItem[] newArray(int size) {
            return new OrderItem[size];
        }
    };

    // Equals and HashCode for comparing objects
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItem orderItem = (OrderItem) o;
        return quantity == orderItem.quantity && Double.compare(orderItem.price, price) == 0 && Objects.equals(name, orderItem.name) && Objects.equals(parcelStatus, orderItem.parcelStatus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, quantity, price, parcelStatus);
    }
}