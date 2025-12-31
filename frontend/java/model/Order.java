package com.simats.foodstall.model;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;

public class Order implements Parcelable {
    private String stallName;
    private String orderId;
    private String orderTime;
    private String status;
    private double totalPrice;
    private ArrayList<OrderItem> orderedItems;
    private String paymentMethod;
    private String cancellationReason;
    private String orderType;

    public Order(String stallName, String orderId, String orderTime, String status, double totalPrice, String paymentMethod, String cancellationReason, String orderType, ArrayList<OrderItem> orderedItems) {
        this.stallName = stallName;
        this.orderId = orderId;
        this.orderTime = orderTime;
        this.status = status;
        this.totalPrice = totalPrice;
        this.paymentMethod = paymentMethod;
        this.cancellationReason = cancellationReason;
        this.orderType = orderType;
        this.orderedItems = orderedItems;
    }

    // Getters
    public String getStallName() { return stallName; }
    public String getOrderId() { return orderId; }
    public String getOrderTime() { return orderTime; }
    public String getStatus() { return status; }
    public double getTotalPrice() { return totalPrice; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getCancellationReason() { return cancellationReason; }
    public ArrayList<OrderItem> getOrderedItems() { return orderedItems; }
    public String getOrderType() { return orderType; }

    // Setter
    public void setStatus(String status) {
        this.status = status;
    }

    // Parcelable Implementation
    protected Order(Parcel in) {
        stallName = in.readString();
        orderId = in.readString();
        orderTime = in.readString();
        status = in.readString();
        totalPrice = in.readDouble();
        paymentMethod = in.readString();
        cancellationReason = in.readString();
        orderType = in.readString();
        orderedItems = in.createTypedArrayList(OrderItem.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(stallName);
        dest.writeString(orderId);
        dest.writeString(orderTime);
        dest.writeString(status);
        dest.writeDouble(totalPrice);
        dest.writeString(paymentMethod);
        dest.writeString(cancellationReason);
        dest.writeString(orderType);
        dest.writeTypedList(orderedItems);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };
}