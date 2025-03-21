package com.example.feed.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.feed.model.AdminFeedbackTrigger;
import com.example.feed.model.Feedback;
import com.example.feed.model.RatingSummary;
import com.example.feed.repository.AdminFeedbackTriggerRepository;
import com.example.feed.repository.FeedbackRepository;
import com.example.feed.repository.RatingSummaryRepository;
@Service
public class AdminService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private RatingSummaryRepository ratingSummaryRepository;

    @Autowired
    private AdminFeedbackTriggerRepository adminFeedbackTriggerRepository;

    private static Boolean isFeedbackEnabled = true;

    public List<Feedback> getAllFeedback() {
        return feedbackRepository.findByIsDeletedFalse();
    }

    public void moderateFeedback(Long feedbackId, Boolean isDeleted) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new RuntimeException("Feedback not found"));
        feedback.setIsDeleted(isDeleted);
        feedbackRepository.save(feedback);
        // Update rating summary after feedback moderation
        updateRatingSummary();
    }

    public void enableFeedback(Boolean isEnabled) {
        isFeedbackEnabled = isEnabled;
    }

    public Boolean getFeedbackStatus() {
        return isFeedbackEnabled;
    }

    public void setFeedbackTrigger(AdminFeedbackTrigger trigger) {
        adminFeedbackTriggerRepository.save(trigger);
    }

    public List<AdminFeedbackTrigger> getAllTriggers() {
        return adminFeedbackTriggerRepository.findAll();
    }
     public void updateRatingSummary() {
        List<Feedback> feedbackList = feedbackRepository.findByIsDeletedFalse();
        double totalRating = feedbackList.stream().mapToInt(Feedback::getRating).sum();
        double averageRating = totalRating / feedbackList.size();

        RatingSummary ratingSummary = ratingSummaryRepository.findById(1L).orElse(new RatingSummary());
        ratingSummary.setOverallRating(averageRating);
        ratingSummaryRepository.save(ratingSummary);
    }

    public void updateLowRatingPercentage() {
        List<Feedback> feedbackList = feedbackRepository.findByIsDeletedFalse();
        long lowRatingCount = feedbackList.stream().filter(feedback -> feedback.getRating() <= 2).count();
        double lowRatingPercentage = (lowRatingCount / (double) feedbackList.size()) * 100;

        RatingSummary ratingSummary = ratingSummaryRepository.findById(1L).orElse(new RatingSummary());
        ratingSummary.setLowRatingPercentage(lowRatingPercentage);
        ratingSummaryRepository.save(ratingSummary);
    }
    public void permanentlyDeleteFeedback(Long feedbackId) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new RuntimeException("Feedback not found"));
        feedbackRepository.delete(feedback);
    }
}
