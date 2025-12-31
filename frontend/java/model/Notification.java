package com.simats.foodstall.model;

public class Notification {
    private String type; // e.g., "New", "Offer", "Popular"
    private String title;
    private String message;
    private String metadata; // e.g., "500m away"
    private String timestamp;
    private boolean isRead; // NEW: To track read/unread status

    public Notification(String type, String title, String message, String metadata, String timestamp, boolean isRead) {
        this.type = type;
        this.title = title;
        this.message = message;
        this.metadata = metadata;
        this.timestamp = timestamp;
        this.isRead = isRead;
    }

    public String getType() { return type; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getMetadata() { return metadata; }
    public String getTimestamp() { return timestamp; }
    public boolean isRead() { return isRead; }

    public void setRead(boolean read) { isRead = read; }
}