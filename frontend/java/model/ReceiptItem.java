package com.simats.foodstall.model;

public class ReceiptItem {
    private String name;
    private int quantity;
    private double price;
    private boolean isParcel;

    public ReceiptItem(String name, int quantity, double price, boolean isParcel) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.isParcel = isParcel;
    }
    // Getters
    public String getName() { return name; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public boolean isParcel() { return isParcel; }
}