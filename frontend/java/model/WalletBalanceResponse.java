package com.simats.foodstall.model;

import com.google.gson.annotations.SerializedName;

public class WalletBalanceResponse {

    @SerializedName("status")
    private String status;

    @SerializedName("balance")
    private double balance;

    public String getStatus() {
        return status;
    }

    public double getBalance() {
        return balance;
    }
}