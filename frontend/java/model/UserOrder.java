package com.simats.foodstall.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;

public class UserOrder implements Parcelable {

    @SerializedName("order_id")
    private int orderId;

    @SerializedName("display_order_id")
    private String displayOrderId;

    @SerializedName("stallname")
    private String stallName;

    @SerializedName("order_date")
    private String orderDate;

    @SerializedName("order_status")
    private String orderStatus;

    @SerializedName("total_amount")
    private double totalAmount;

    @SerializedName("subtotal")
    private double subtotal;

    @SerializedName("parcel_fee")
    private double parcelFee;

    @SerializedName("payment_method")
    private String paymentMethod;

    @SerializedName("cancellation_reason")
    private String cancellationReason;

    @SerializedName("pickup_time")
    private String pickupTime;

    @SerializedName("items_json")
    private String itemsJson;

    // --- NEW FIELD ADDED FOR REFUND DETAILS ---
    @SerializedName("refund_timestamp")
    private String refundTimestamp;
    // ------------------------------------------

    public List<UserOrderItem> getItems() {
        if (itemsJson == null || itemsJson.isEmpty()) {
            return new ArrayList<>();
        }
        return new Gson().fromJson(itemsJson, new TypeToken<List<UserOrderItem>>(){}.getType());
    }

    // Getters
    public int getOrderId() { return orderId; }
    public String getDisplayOrderId() { return displayOrderId; }
    public String getStallName() { return stallName; }
    public String getOrderDate() { return orderDate; }
    public String getOrderStatus() { return orderStatus; }
    public double getTotalAmount() { return totalAmount; }
    public double getSubtotal() { return subtotal; }
    public double getParcelFee() { return parcelFee; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getCancellationReason() { return cancellationReason; }
    public String getPickupTime() { return pickupTime; }
    // --- NEW GETTER FOR REFUND TIMESTAMP ---
    public String getRefundTimestamp() { return refundTimestamp; }
    // ---------------------------------------


    // Parcelable Implementation
    protected UserOrder(Parcel in) {
        orderId = in.readInt();
        displayOrderId = in.readString();
        stallName = in.readString();
        orderDate = in.readString();
        orderStatus = in.readString();
        totalAmount = in.readDouble();
        subtotal = in.readDouble();
        parcelFee = in.readDouble();
        paymentMethod = in.readString();
        cancellationReason = in.readString();
        pickupTime = in.readString();
        itemsJson = in.readString();
        // --- READ NEW FIELD ---
        refundTimestamp = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(orderId);
        dest.writeString(displayOrderId);
        dest.writeString(stallName);
        dest.writeString(orderDate);
        dest.writeString(orderStatus);
        dest.writeDouble(totalAmount);
        dest.writeDouble(subtotal);
        dest.writeDouble(parcelFee);
        dest.writeString(paymentMethod);
        dest.writeString(cancellationReason);
        dest.writeString(pickupTime);
        dest.writeString(itemsJson);
        // --- WRITE NEW FIELD ---
        dest.writeString(refundTimestamp);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserOrder> CREATOR = new Creator<UserOrder>() {
        @Override
        public UserOrder createFromParcel(Parcel in) {
            return new UserOrder(in);
        }

        @Override
        public UserOrder[] newArray(int size) {
            return new UserOrder[size];
        }
    };
}