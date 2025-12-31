package com.simats.foodstall.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class RevenueResponse {

    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private RevenueData data;

    public String getStatus() {
        return status;
    }

    public RevenueData getData() {
        return data;
    }

    public static class RevenueData {
        @SerializedName("todays_revenue")
        private double todaysRevenue;

        @SerializedName("this_weeks_revenue")
        private double thisWeeksRevenue;

        @SerializedName("this_months_revenue")
        private double thisMonthsRevenue;

        @SerializedName("daily_details")
        private List<RevenueDetail> dailyDetails;

        public double getTodaysRevenue() {
            return todaysRevenue;
        }

        public double getThisWeeksRevenue() {
            return thisWeeksRevenue;
        }

        public double getThisMonthsRevenue() {
            return thisMonthsRevenue;
        }

        public List<RevenueDetail> getDailyDetails() {
            return dailyDetails;
        }
    }
}