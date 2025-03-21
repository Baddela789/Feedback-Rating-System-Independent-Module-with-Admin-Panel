package com.example.feed.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.feed.model.Feedback;
import com.example.feed.model.UserActivity;
import com.example.feed.service.FeedbackService;
import com.example.feed.service.UserActivityService;

@RestController
@RequestMapping("/api/user/feedback")
@CrossOrigin("*")
public class UserController {

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private UserActivityService userActivityService;

    @PostMapping("/submit")
    public ResponseEntity<Feedback> submitFeedback(@RequestBody Feedback feedback) {
        return ResponseEntity.ok(feedbackService.createFeedback(
                feedback.getUserId(),
                feedback.getFeedbackText(),
                feedback.getRating(),
                feedback.getIsAnonymous(),
                feedback.getComments(),
                feedback.getActivityType()));
    }
    // @PostMapping("/submit")
    // public ResponseEntity<Feedback> submitFeedback(@RequestBody FeedbackRequest
    // feedbackRequest) {
    // // if (feedbackRequest.getUserId() == null ||
    // feedbackRequest.getFeedbackText().isEmpty()
    // // || feedbackRequest.getActivityType().isEmpty()) {
    // // throw new IllegalArgumentException("User ID, feedback text, and activity
    // type are required.");
    // // }

    // Feedback savedFeedback = feedbackService.createFeedback(
    // feedbackRequest.getUserId(),
    // feedbackRequest.getFeedbackText(),
    // feedbackRequest.getRating(),
    // feedbackRequest.isAnonymous(),
    // feedbackRequest.getComments(),
    // feedbackRequest.getActivityType());

    // return ResponseEntity.ok(savedFeedback);
    // }

    // @PostMapping("/submit")
    // public ResponseEntity<String> submitFeedback(@RequestBody FeedbackRequest
    // feedbackRequest) {
    // feedbackService.submitFeedback(feedbackRequest);
    // return ResponseEntity.ok("Feedback submitted successfully");
    // }
    @PostMapping("/track")
    public ResponseEntity<Map<String, Object>> trackUserActivity(@RequestBody Map<String, Object> requestBody) {
        Long userId = Long.valueOf(requestBody.get("userId").toString());
        String activityType = requestBody.get("activityType").toString();

        boolean shouldTriggerFeedback = userActivityService.trackUserActivity(userId, activityType);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Activity recorded successfully.");
        response.put("triggerFeedback", shouldTriggerFeedback);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/view/{userId}")
    public List<Feedback> viewUserFeedback(@PathVariable Long userId) {
    return feedbackService.getUserFeedback(userId);
    }

    @PutMapping("/update/{feedbackId}")
    public ResponseEntity<Feedback> updateFeedback(@PathVariable Long feedbackId,
    @RequestBody Feedback feedback) {
    return ResponseEntity
    .ok(feedbackService.updateFeedback(feedbackId, feedback.getFeedbackText(),
    feedback.getRating()));
    }
    @PostMapping("/view")
    public ResponseEntity<?> viewUserFeedback(@RequestBody Map<String, Object> requestBody) {
        Long userId = ((Number) requestBody.get("userId")).longValue();
        if (userId == null) {
            return ResponseEntity.badRequest().body("User ID is required");
        }
        return ResponseEntity.ok(feedbackService.getUserFeedback(userId));
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateFeedback(@RequestBody FeedbackUpdateRequest request) {
        if (request.getFeedbackId() == null) {
            return ResponseEntity.badRequest().body("Feedback ID is required");
        }
        return ResponseEntity.ok(feedbackService.updateFeedback(request));
    }

    public static class FeedbackUpdateRequest {
        private Long feedbackId;
        private String feedbackText;
        private Integer rating;

        public String getFeedbackText() {
            return feedbackText;
        }

        public void setFeedbackText(String feedbackText) {
            this.feedbackText = feedbackText;
        }

        public Integer getRating() {
            return rating;
        }

        public void setRating(Integer rating) {
            this.rating = rating;
        }

        public String getComments() {
            return comments;
        }

        public void setComments(String comments) {
            this.comments = comments;
        }

        private String comments;

        public Long getFeedbackId() {
            return feedbackId;
        }

        public void setFeedbackId(Long feedbackId) {
            this.feedbackId = feedbackId;
        }

    }

    @PostMapping("/delete/{feedbackId}")
    public ResponseEntity<String> deleteFeedback(@PathVariable Long feedbackId) {
        feedbackService.deleteFeedback(feedbackId);
        return ResponseEntity.ok("Feedback deleted successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        // Authenticate user using the UserService
        UserActivity user = userActivityService.authenticateUser(request.getEmail(), request.getPassword());

        if (user == null) {
            return ResponseEntity.status(401).body("Invalid email or password");
        }

        // If login is successful, return userId and role
        return ResponseEntity.ok(new LoginResponse(user.getId(), "Login successful", user.getRole()));
    }

    // Request body for login API
    public static class LoginRequest {
        private String email;
        private String password;

        // Getters and Setters
        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    // Response body for login API
    public static class LoginResponse {
        private Long userId;
        private String message;
        private String role;

        public LoginResponse(Long userId, String message, String role) {
            this.userId = userId;
            this.message = message;
            this.role = role;
        }

        // Getters and Setters
        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }
}
