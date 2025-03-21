package com.example.feed.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.feed.model.AdminFeedbackTrigger;
import com.example.feed.model.Feedback;
import com.example.feed.service.AdminAnalyticsService;
import com.example.feed.service.AdminService;
import com.example.feed.service.FeedbackService;

@RestController
@RequestMapping("/api/admin/feedback")
@CrossOrigin("*")
public class AdminController {

    @Autowired
    private AdminService adminService;
    @Autowired
    private FeedbackService feedbackService;
    @Autowired
    private AdminAnalyticsService adminAnalyticsService;
    @PostMapping("/view-all")
    public List<Feedback> viewAllFeedback() {
        return adminService.getAllFeedback();
    }
    public static class FeedbackModerationRequest {
        private Boolean isDeleted;

        public Boolean getIsDeleted() {
            return isDeleted;
        }

        public void setIsDeleted(Boolean isDeleted) {
            this.isDeleted = isDeleted;
        }
    
        // Getter and setter
    }
    @PostMapping("/moderate/{feedbackId}")
    public ResponseEntity<String> moderateFeedback(@PathVariable Long feedbackId, @RequestBody FeedbackModerationRequest request) {
        adminService.moderateFeedback(feedbackId, request.getIsDeleted());
        return ResponseEntity.ok("Feedback moderation updated successfully");
    }

    // Set feedback trigger
    @PostMapping("/set-trigger")
    public ResponseEntity<String> setFeedbackTrigger(@RequestBody AdminFeedbackTrigger trigger) {
        adminService.setFeedbackTrigger(trigger);
        return ResponseEntity.ok("Feedback trigger set successfully");
    }

    // View all triggers
    @PostMapping("/view-triggers")
    public List<AdminFeedbackTrigger> viewAllTriggers() {
        return adminService.getAllTriggers();
    }

    @PostMapping("/enable-feedback")
    public ResponseEntity<String> enableFeedback(@RequestBody EnableFeedbackRequest request) {
        adminService.enableFeedback(request.getIsEnabled());
        return ResponseEntity.ok("Feedback system status updated successfully");
    }

    // Get the current feedback system status
    @GetMapping("/feedback-status")
    public ResponseEntity<Boolean> getFeedbackStatus() {
        return ResponseEntity.ok(adminService.getFeedbackStatus());
    }

    // Make EnableFeedbackRequest static
    public static class EnableFeedbackRequest {
        private Boolean isEnabled;

        public Boolean getIsEnabled() {
            return isEnabled;
        }

        public void setIsEnabled(Boolean isEnabled) {
            this.isEnabled = isEnabled;
        }
    }
    //   @DeleteMapping("/delete/{feedbackId}")
    // public ResponseEntity<String> permanentlyDeleteFeedback(@PathVariable Long feedbackId) {
    //     adminService.permanentlyDeleteFeedback(feedbackId);
    //     return ResponseEntity.ok("Feedback deleted permanently");
    // }
    @PostMapping("/delete")
    public ResponseEntity<?>permanentlyDeleteFeedback(@RequestBody Map<String, Object> requestBody) {
        Long feedbackId = ((Number) requestBody.get("feedbackId")).longValue();
        if (feedbackId == null) {
            return ResponseEntity.badRequest().body("Feedback ID is required");
        }
        adminService.permanentlyDeleteFeedback(feedbackId);
        return ResponseEntity.ok("Feedback deleted successfully");
    }
    @PostMapping("/analytics-report")
    public ResponseEntity<Map<String, Object>> getAnalyticsReport() {
        return ResponseEntity.ok(adminAnalyticsService.getAnalyticsReport());
    }
}
