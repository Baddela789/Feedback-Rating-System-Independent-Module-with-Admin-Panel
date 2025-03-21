package com.example.feed.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class AdminFeedbackTrigger {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    private String activityType; 
    public String getActivityType() {
        return activityType;
    }
    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }
    private Integer triggerCount; 
    public Integer getTriggerCount() {
        return triggerCount;
    }
    public void setTriggerCount(Integer triggerCount) {
        this.triggerCount = triggerCount;
    }
    private Boolean isFeedbackMandatory;
    public Boolean getIsFeedbackMandatory() {
        return isFeedbackMandatory;
    }
    public void setIsFeedbackMandatory(Boolean isFeedbackMandatory) {
        this.isFeedbackMandatory = isFeedbackMandatory;
    }
    private Boolean isRatingRequired;
    public Boolean getIsRatingRequired() {
        return isRatingRequired;
    }
    public void setIsRatingRequired(Boolean isRatingRequired) {
        this.isRatingRequired = isRatingRequired;
    }
    private Boolean isCommentsRequired;
    public Boolean getIsCommentsRequired() {
        return isCommentsRequired;
    }
    public void setIsCommentsRequired(Boolean isCommentsRequired) {
        this.isCommentsRequired = isCommentsRequired;
    }

    
}