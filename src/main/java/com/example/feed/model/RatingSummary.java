package com.example.feed.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class RatingSummary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    private Double overallRating;
    public Double getOverallRating() {
        return overallRating;
    }
    public void setOverallRating(Double overallRating) {
        this.overallRating = overallRating;
    }
    private Double lowRatingPercentage;
    public Double getLowRatingPercentage() {
        return lowRatingPercentage;
    }
    public void setLowRatingPercentage(Double lowRatingPercentage) {
        this.lowRatingPercentage = lowRatingPercentage;
    }

}