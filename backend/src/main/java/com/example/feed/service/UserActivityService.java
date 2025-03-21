package com.example.feed.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.feed.model.AdminFeedbackTrigger;
import com.example.feed.model.UserActivity;
import com.example.feed.repository.AdminFeedbackTriggerRepository;
import com.example.feed.repository.UserActivityRepository;

@Service
public class UserActivityService {

    @Autowired
    private UserActivityRepository userActivityRepository;

    @Autowired
    private AdminFeedbackTriggerRepository adminFeedbackTriggerRepository;

    public boolean trackUserActivity(Long userId, String activityType) {
        // Check if a trigger exists for this activity type
        Optional<AdminFeedbackTrigger> triggerOpt = adminFeedbackTriggerRepository.findAll()
                .stream()
                .filter(t -> t.getActivityType().equalsIgnoreCase(activityType))
                .findFirst();

        if (triggerOpt.isEmpty()) {
            return false; // No trigger set for this activity
        }

        AdminFeedbackTrigger trigger = triggerOpt.get();

        // Find existing activity record for user
        Optional<UserActivity> userActivityOpt = userActivityRepository.findByUserIdAndActivityType(userId,
                activityType);

        UserActivity userActivity;
        if (userActivityOpt.isPresent()) {
            userActivity = userActivityOpt.get();
            userActivity.setActivityCount(userActivity.getActivityCount() + 1);
        } else {
            userActivity = new UserActivity();
            userActivity.setUserId(userId);
            userActivity.setActivityType(activityType);
            userActivity.setActivityCount(1);
        }

        userActivityRepository.save(userActivity);

        // Check if user has reached the trigger count
        return userActivity.getActivityCount() >= trigger.getTriggerCount();
    }

     // Method to authenticate user
     public UserActivity authenticateUser(String email, String password) {
        // Find user by email
        UserActivity user = userActivityRepository.findByEmail(email);
        if (user == null || !user.getPassword().equals(password)) {
            return null;  // If user not found or password doesn't match, return null
        }
        return user;  // Return the user object if authentication is successful
    }
}
