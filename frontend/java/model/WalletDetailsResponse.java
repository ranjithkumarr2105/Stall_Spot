package com.simats.foodstall.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * This class represents the JSON response from the Uget_wallet_details.php script.
 * It contains both the user's balance and their transaction history.
 */
public class WalletDetailsResponse {

    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private WalletData data;

    public String getStatus() { return status; }
    public WalletData getData() { return data; }

    /**
     * This nested class holds the actual wallet data.
     */
    public static class WalletData {

        @SerializedName("balance")
        private double balance;

        @SerializedName("transactions")
        private List<Transaction> transactions;

        public double getBalance() { return balance; }
        public List<Transaction> getTransactions() { return transactions; }
    }
}
