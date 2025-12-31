package com.simats.foodstall.model;

public class AdminProfile {
    public String name;
    public String email;
    public String contact;
    public String role;

    public AdminProfile(String name, String email, String contact, String role) {
        this.name = name;
        this.email = email;
        this.contact = contact;
        this.role = role;
    }
}