package com.simats.foodstall.model;

import java.util.List;

/**
 * This class holds all information needed for a single order receipt.
 * It acts as a container for the main order details and a list of OrderItem objects.
 */
public class OrderDetails {
    private String orderId;
    private String studentId;
    private String orderType; // Can be "Parcel", "Pre-Parcel", or "Dine-In"
    private String endTime;   // This is only used for "Pre-Parcel" orders.
    private List<OrderItem> items;

    public OrderDetails(String orderId, String studentId, String orderType, String endTime, List<OrderItem> items) {
        this.orderId = orderId;
        this.studentId = studentId;
        this.orderType = orderType;
        this.endTime = endTime;
        this.items = items;
    }

    // --- Getters to access the private fields ---
    public String getOrderId() {
        return orderId;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getOrderType() {
        return orderType;
    }

    public String getEndTime() {
        return endTime;
    }

    public List<OrderItem> getItems() {
        return items;
    }
}