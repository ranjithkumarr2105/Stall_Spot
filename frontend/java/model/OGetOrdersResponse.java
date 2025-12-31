package com.simats.foodstall.model;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class OGetOrdersResponse {
    @SerializedName("status") private String status;
    @SerializedName("orders") private List<OOrder> orders;
    public String getStatus() { return status; }
    public List<OOrder> getOrders() { return orders; }
}