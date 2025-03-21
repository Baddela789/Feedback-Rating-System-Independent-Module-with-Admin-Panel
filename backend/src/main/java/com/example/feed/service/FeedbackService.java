package com.example.feed.service;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.feed.controller.UserController.FeedbackUpdateRequest;
import com.example.feed.model.Feedback;
import com.example.feed.repository.AdminFeedbackTriggerRepository;
import com.example.feed.repository.FeedbackRepository;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;
    @Autowired
    private AdminFeedbackTriggerRepository adminFeedbackTriggerRepository;

    @Autowired
    private AdminService adminService;

    public Feedback createFeedback(Long userId, String feedbackText, Integer rating, Boolean isAnonymous,
            String comments, String activityType) {
        if (!adminService.getFeedbackStatus()) {
            throw new RuntimeException("Feedback submission is currently disabled.");
        }

        Feedback feedback = new Feedback();

        if (!Boolean.TRUE.equals(isAnonymous)) {
            feedback.setUserId(userId);
        }
        feedback.setActivityType(activityType);
        feedback.setFeedbackText(feedbackText);
        feedback.setRating(rating);
        feedback.setIsAnonymous(isAnonymous);
        feedback.setCreatedAt(ZonedDateTime.now());
        feedback.setUpdatedAt(ZonedDateTime.now());
        feedback.setComments(comments);

        Feedback savedFeedback = feedbackRepository.save(feedback);

       
        return savedFeedback;
        

    }
    // public Feedback createFeedback(Long userId, String feedbackText, Integer
    // rating, Boolean isAnonymous, String comments, String activityType) {
    // if (!adminService.getFeedbackStatus()) {
    // throw new RuntimeException("Feedback submission is currently disabled.");
    // }

    // Feedback feedback = new Feedback();

    // feedback.setIsAnonymous(isAnonymous);

    // if (!Boolean.TRUE.equals(isAnonymous)) {
    // feedback.setUserId(userId);
    // } else {
    // feedback.setUserId(null);
    // }

    // feedback.setActivityType(activityType);
    // feedback.setFeedbackText(feedbackText);
    // feedback.setRating(rating);
    // feedback.setCreatedAt(ZonedDateTime.now());
    // feedback.setUpdatedAt(ZonedDateTime.now());
    // feedback.setComments(comments);

    // Feedback savedFeedback = feedbackRepository.save(feedback);

    // adminService.updateRatingSummary();
    // adminService.updateLowRatingPercentage();

    // return savedFeedback;
    // }

    // public Feedback updateFeedback(Long feedbackId, String feedbackText, Integer
    // rating) {
    // Feedback feedback = feedbackRepository.findById(feedbackId)
    // .orElseThrow(() -> new RuntimeException("Feedback not found"));
    // feedback.setFeedbackText(feedbackText);
    // feedback.setRating(rating);
    // feedback.setUpdatedAt(ZonedDateTime.now());

    // Feedback updatedFeedback = feedbackRepository.save(feedback);

    // // Update rating summary after feedback update
    // adminService.updateRatingSummary();
    // adminService.updateLowRatingPercentage();

    // return updatedFeedback;
    // }

    public void deleteFeedback(Long feedbackId) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new RuntimeException("Feedback not found"));
        feedback.setIsDeleted(true);
        feedbackRepository.save(feedback);

        // Update rating summary after feedback deletion
        adminService.updateRatingSummary();
        adminService.updateLowRatingPercentage();
    }

    public Feedback updateFeedback(FeedbackUpdateRequest request) {
        Feedback feedback = feedbackRepository.findById(request.getFeedbackId())
                .orElseThrow(() -> new RuntimeException("Feedback not found"));
        feedback.setFeedbackText(request.getFeedbackText());
        feedback.setRating(request.getRating());
        feedback.setComments(request.getComments());
        feedback.setUpdatedAt(ZonedDateTime.now());

        Feedback updatedFeedback = feedbackRepository.save(feedback);

        adminService.updateRatingSummary();
        adminService.updateLowRatingPercentage();

        return updatedFeedback;
    }

    public List<Feedback> getUserFeedback(Long userId) {
        return feedbackRepository.findByUserId(userId);
    }

    // public List<Feedback> getUserFeedback(Long userId) {
    // return feedbackRepository.findByUserId(userId);
    // }

    public List<Feedback> getAllFeedback() {
        return feedbackRepository.findByIsDeletedFalse();
    }

}
