package com.simats.foodstall.model;

public class AnalysisComparison {
    public String stallName;
    public String pastMonth;
    public String presentMonth;
    public int percentageChange;

    public AnalysisComparison(String stallName, String pastMonth, String presentMonth, int percentageChange) {
        this.stallName = stallName;
        this.pastMonth = pastMonth;
        this.presentMonth = presentMonth;
        this.percentageChange = percentageChange;
    }
}