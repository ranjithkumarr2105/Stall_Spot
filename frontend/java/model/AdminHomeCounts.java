package com.simats.foodstall.model;

import com.google.gson.annotations.SerializedName;

public class AdminHomeCounts {

    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private CountsData data;

    public String getStatus() { return status; }
    public CountsData getData() { return data; }

    public static class CountsData {
        @SerializedName("total_stalls")
        private int totalStalls;

        @SerializedName("approved_stalls")
        private int approvedStalls;

        @SerializedName("rejected_stalls")
        private int rejectedStalls;

        @SerializedName("pending_stalls")
        private int pendingStalls;

        public int getTotalStalls() { return totalStalls; }
        public int getApprovedStalls() { return approvedStalls; }
        public int getRejectedStalls() { return rejectedStalls; }
        public int getPendingStalls() { return pendingStalls; }
    }
}