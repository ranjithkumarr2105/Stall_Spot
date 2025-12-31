package com.simats.foodstall.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AnalysisData {

    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private Data data;

    public String getStatus() { return status; }
    public Data getData() { return data; }

    public static class Data {
        @SerializedName("top_stalls")
        private List<TopStall> topStalls;

        @SerializedName("comparison")
        private List<ComparisonItem> comparison;

        public List<TopStall> getTopStalls() { return topStalls; }
        public List<ComparisonItem> getComparison() { return comparison; }
    }

    public static class TopStall {
        @SerializedName("stallname")
        private String stallName;

        @SerializedName("current_revenue")
        private double currentRevenue;

        public String getStallName() { return stallName; }
        public double getCurrentRevenue() { return currentRevenue; }
    }

    public static class ComparisonItem {
        @SerializedName("stall_id")
        private String stallId;

        @SerializedName("stallname")
        private String stallName;

        @SerializedName("current_month_revenue")
        private double currentMonthRevenue;

        @SerializedName("previous_month_revenue")
        private double previousMonthRevenue;

        public String getStallId() { return stallId; }
        public String getStallName() { return stallName; }
        public double getCurrentMonthRevenue() { return currentMonthRevenue; }
        public double getPreviousMonthRevenue() { return previousMonthRevenue; }
    }
}