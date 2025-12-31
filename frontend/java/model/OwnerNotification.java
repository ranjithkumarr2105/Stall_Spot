package com.simats.foodstall.model;

public class OwnerNotification {
    public String title;
    public String message;
    public String time;
    public int iconResId;

    public OwnerNotification(String title, String message, String time, int iconResId) {
        this.title = title;
        this.message = message;
        this.time = time;
        this.iconResId = iconResId;
    }
}