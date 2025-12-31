package com.simats.foodstall.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class OGetMenuResponse {
    @SerializedName("status") private String status;
    @SerializedName("data") private MenuData data;

    public String getStatus() { return status; }
    public MenuData getData() { return data; }

    public static class MenuData {
        @SerializedName("stall_details") private StallDetails stallDetails;
        @SerializedName("menu_items") private List<OMenuItem> menuItems;

        public StallDetails getStallDetails() { return stallDetails; }
        public List<OMenuItem> getMenuItems() { return menuItems; }
    }

    public static class StallDetails {
        @SerializedName("approval") private int approval;
        @SerializedName("is_open_today") private int isOpenToday;
        @SerializedName("opening_hours") private String openingHours;
        @SerializedName("closing_hours") private String closingHours;

        public int getApproval() { return approval; }
        public boolean isOpenToday() { return isOpenToday == 1; }
        public String getOpeningHours() { return openingHours; }
        public String getClosingHours() { return closingHours; }
    }
}