package com.simats.foodstall.model;

public class AdminReview {
    public String stallName;
    public String ownerName;
    public String message;
    public String date;
    public int rating;

    public AdminReview(String stallName, String ownerName, String message, String date, int rating) {
        this.stallName = stallName;
        this.ownerName = ownerName;
        this.message = message;
        this.date = date;
        this.rating = rating;
    }
}