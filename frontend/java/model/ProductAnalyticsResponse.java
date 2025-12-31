package com.simats.foodstall.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ProductAnalyticsResponse {
    @SerializedName("status")
    private String status;
    @SerializedName("data")
    private AnalyticsData data;

    public String getStatus() { return status; }
    public AnalyticsData getData() { return data; }

    public static class AnalyticsData {
        @SerializedName("total_revenue")
        private double totalRevenue;
        @SerializedName("total_orders")
        private int totalOrders;
        @SerializedName("top_performers")
        private List<TopPerformer> topPerformers;
        @SerializedName("needs_attention")
        private List<NeedsAttentionItem> needsAttention;

        public double getTotalRevenue() { return totalRevenue; }
        public int getTotalOrders() { return totalOrders; }
        public List<TopPerformer> getTopPerformers() { return topPerformers; }
        public List<NeedsAttentionItem> getNeedsAttention() { return needsAttention; }
    }
}