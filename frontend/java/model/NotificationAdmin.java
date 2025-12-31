package com.simats.foodstall.model;

public class NotificationAdmin {
    private String title;
    private String message;
    private String time;
    private boolean isNew;

    public NotificationAdmin(String title, String message, String time, boolean isNew) {
        this.title = title;
        this.message = message;
        this.time = time;
        this.isNew = isNew;
    }

    // Getters for all fields
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getTime() { return time; }
    public boolean isNew() { return isNew; }
}