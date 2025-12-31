package com.simats.foodstall;

public class WalletManager {

    private static double balance = 120.00; // Starting balance

    public static double getBalance() {
        return balance;
    }

    public static void addMoney(double amount) {
        if (amount > 0) {
            balance += amount;
        }
    }
}