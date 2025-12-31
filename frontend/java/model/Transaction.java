package com.simats.foodstall.model;

import com.google.gson.annotations.SerializedName;
import java.util.Objects;

public class Transaction {

    @SerializedName("transaction_id")
    private int transactionId;

    @SerializedName("transaction_type")
    private String transactionType;

    @SerializedName("amount")
    private double amount;

    @SerializedName("description")
    private String description;

    @SerializedName("transaction_date")
    private String transactionDate;

    public int getTransactionId() { return transactionId; }
    public String getTransactionType() { return transactionType; }
    public double getAmount() { return amount; }
    public String getDescription() { return description; }
    public String getTransactionDate() { return transactionDate; }

    // These methods are essential for the multi-select delete feature to work correctly
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return transactionId == that.transactionId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionId);
    }
}