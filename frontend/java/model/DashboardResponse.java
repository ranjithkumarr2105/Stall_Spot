package com.simats.foodstall.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class DashboardResponse {

    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private DashboardData data;

    public String getStatus() { return status; }
    public DashboardData getData() { return data; }

    public static class DashboardData {

        @SerializedName("orders_today")
        private int ordersToday;

        @SerializedName("revenue_today")
        private double revenueToday;

        @SerializedName("pending_orders")
        private int pendingOrders;

        @SerializedName("top_selling")
        private String topSelling;

        @SerializedName("revenue_trend")
        private List<RevenueTrendPoint> revenueTrend;

        @SerializedName("peak_hours")
        private List<PeakHourPoint> peakHours;

        @SerializedName("rent_details")
        private RentDetails rentDetails;

        public int getOrdersToday() { return ordersToday; }
        public double getRevenueToday() { return revenueToday; }
        public int getPendingOrders() { return pendingOrders; }
        public String getTopSelling() { return topSelling; }
        public List<RevenueTrendPoint> getRevenueTrend() { return revenueTrend; }
        public List<PeakHourPoint> getPeakHours() { return peakHours; }
        public RentDetails getRentDetails() { return rentDetails; }
    }

    public static class RevenueTrendPoint {
        @SerializedName("date")
        private String date;
        @SerializedName("revenue")
        private double revenue;

        public String getDate() { return date; }
        public double getRevenue() { return revenue; }
    }

    public static class PeakHourPoint {
        @SerializedName("hour")
        private int hour;
        @SerializedName("order_count")
        private int orderCount;

        public int getHour() { return hour; }
        public int getOrderCount() { return orderCount; }
    }

    public static class RentDetails {
        @SerializedName("invoice_id")
        private int invoiceId;

        @SerializedName("total_revenue")
        private double totalRevenue;

        // --- UPDATED: Changed from primitive 'double' to wrapper 'Double' ---
        @SerializedName("rent_amount")
        private Double rentAmount;

        @SerializedName("late_fee")
        private Double lateFee;
        // --- END OF UPDATE ---

        @SerializedName("invoice_month")
        private int invoiceMonth;

        @SerializedName("invoice_year")
        private int invoiceYear;

        public int getInvoiceId() { return invoiceId; }
        public double getTotalRevenue() { return totalRevenue; }

        // --- UPDATED: Getters now return the wrapper type 'Double' ---
        public Double getRentAmount() { return rentAmount; }
        public Double getLateFee() { return lateFee; }
        // --- END OF UPDATE ---

        public int getInvoiceMonth() { return invoiceMonth; }
        public int getInvoiceYear() { return invoiceYear; }
    }
}