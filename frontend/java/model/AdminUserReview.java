package com.simats.foodstall.model;

public class AdminUserReview {
    public String studentId;
    public String studentName;
    public String stallName;
    public String message;
    public String date;
    public int rating;

    public AdminUserReview(String studentId, String studentName, String stallName, String message, String date, int rating) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.stallName = stallName;
        this.message = message;
        this.date = date;
        this.rating = rating;
    }
}