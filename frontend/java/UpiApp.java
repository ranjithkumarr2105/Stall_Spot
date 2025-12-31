package com.simats.foodstall;

public class UpiApp {
    private String name;
    private int logoResId; // To hold the drawable ID for the logo

    public UpiApp(String name, int logoResId) {
        this.name = name;
        this.logoResId = logoResId;
    }

    public String getName() {
        return name;
    }

    public int getLogoResId() {
        return logoResId;
    }
}