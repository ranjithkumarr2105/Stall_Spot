package com.simats.foodstall.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken; // Make sure this import is present

import java.lang.reflect.Type; // Make sure this import is present
import java.util.ArrayList;
import java.util.List;

public class OOrder implements Parcelable {
    @SerializedName("display_order_id")
    private String displayOrderId;
    @SerializedName("student_id")
    private String studentId;
    @SerializedName("total_amount")
    private double totalAmount;
    @SerializedName("subtotal")
    private double subtotal;
    @SerializedName("parcel_fee")
    private double parcelFee;
    @SerializedName("order_status")
    private String orderStatus;
    @SerializedName("stallname")
    private String stallName;
    @SerializedName("order_date")
    private String orderDate;
    @SerializedName("items_summary")
    private String itemsSummary;
    @SerializedName("items_json")
    private String itemsJson;
    @SerializedName("parcel_type")
    private String parcelType;
    @SerializedName("pickup_time")
    private String pickupTime;
    @SerializedName("refund_timestamp")
    private String refundTimestamp;
    private transient List<OrderItem> orderItems; // transient avoids serialization

    // Getters remain the same
    public String getDisplayOrderId() { return displayOrderId; }
    public String getStudentId() { return studentId; }
    public double getTotalAmount() { return totalAmount; }
    public double getSubtotal() { return subtotal; }
    public double getParcelFee() { return parcelFee; }
    public String getOrderStatus() { return orderStatus; }
    public String getStallName() { return stallName; }
    public String getOrderDate() { return orderDate; }
    public String getItemsSummary() { return itemsSummary; }
    public String getItemsJson() { return itemsJson; }
    public String getParcelType() { return parcelType; }
    public String getPickupTime() { return pickupTime; }
    public String getRefundTimestamp() { return refundTimestamp; }

    // --- THIS IS THE ONLY UPDATED METHOD ---
    // It is now safer and correctly populates the transient 'orderItems' list.
    public List<OrderItem> getOrderItems() {
        if (orderItems == null && itemsJson != null && !itemsJson.isEmpty()) {
            try {
                Type listType = new TypeToken<ArrayList<OrderItem>>(){}.getType();
                orderItems = new Gson().fromJson(itemsJson, listType);
            } catch (Exception e) {
                // If there is any error parsing the JSON, return an empty list to prevent a crash
                orderItems = new ArrayList<>();
            }
        }
        // If the list is still null for any reason, create an empty one to be safe.
        if (orderItems == null) {
            orderItems = new ArrayList<>();
        }
        return orderItems;
    }

    // --- Parcelable Implementation (Unchanged) ---
    protected OOrder(Parcel in) {
        displayOrderId = in.readString();
        studentId = in.readString();
        totalAmount = in.readDouble();
        subtotal = in.readDouble();
        parcelFee = in.readDouble();
        orderStatus = in.readString();
        stallName = in.readString();
        orderDate = in.readString();
        itemsSummary = in.readString();
        itemsJson = in.readString();
        parcelType = in.readString();
        pickupTime = in.readString();
        refundTimestamp = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(displayOrderId);
        dest.writeString(studentId);
        dest.writeDouble(totalAmount);
        dest.writeDouble(subtotal);
        dest.writeDouble(parcelFee);
        dest.writeString(orderStatus);
        dest.writeString(stallName);
        dest.writeString(orderDate);
        dest.writeString(itemsSummary);
        dest.writeString(itemsJson);
        dest.writeString(parcelType);
        dest.writeString(pickupTime);
        dest.writeString(refundTimestamp);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<OOrder> CREATOR = new Creator<OOrder>() {
        @Override
        public OOrder createFromParcel(Parcel in) {
            return new OOrder(in);
        }

        @Override
        public OOrder[] newArray(int size) {
            return new OOrder[size];
        }

    };
}