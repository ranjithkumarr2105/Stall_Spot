package com.simats.foodstall.model;

import com.google.gson.annotations.SerializedName;
import java.util.Objects;

public class Review {

    @SerializedName("stallname")
    private String stallName;

    @SerializedName("fullname")
    private String reviewerName;

    @SerializedName("rating")
    private float rating;

    @SerializedName("review_text")
    private String reviewText;

    @SerializedName("review_date")
    private String reviewDate;

    @SerializedName("student_id")
    private String studentId;

    /**
     * This constructor is used by the OwnerReviewsFragment.
     * It includes the stall name.
     */
    public Review(String stallName, String reviewerName, float rating, String reviewText, String reviewDate) {
        this.stallName = stallName;
        this.reviewerName = reviewerName;
        this.rating = rating;
        this.reviewText = reviewText;
        this.reviewDate = reviewDate;
    }

    /**
     * This constructor is used by UviewmenuActivity.
     * It does not need the stall name.
     */
    public Review(String reviewerName, float rating, String reviewText, String reviewDate) {
        this.reviewerName = reviewerName;
        this.rating = rating;
        this.reviewText = reviewText;
        this.reviewDate = reviewDate;
    }

    // Getters
    public String getStallName() { return stallName; }
    public String getReviewerName() { return reviewerName; }
    public float getRating() { return rating; }
    public String getReviewText() { return reviewText; }
    public String getReviewDate() { return reviewDate; }
    public String getStudentId() { return studentId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Review review = (Review) o;
        return Float.compare(review.rating, rating) == 0 &&
                Objects.equals(stallName, review.stallName) &&
                Objects.equals(reviewerName, review.reviewerName) &&
                Objects.equals(reviewText, review.reviewText) &&
                Objects.equals(reviewDate, review.reviewDate) &&
                Objects.equals(studentId, review.studentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stallName, reviewerName, rating, reviewText, reviewDate, studentId);
    }
}