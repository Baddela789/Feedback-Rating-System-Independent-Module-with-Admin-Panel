package com.example.feed.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.feed.model.Feedback;
import com.example.feed.repository.FeedbackRepository;

@Service
public class AdminAnalyticsService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    public Map<String, Object> getAnalyticsReport() {
        List<Feedback> feedbackList = feedbackRepository.findByIsDeletedFalse();

        if (feedbackList.isEmpty()) {
            throw new RuntimeException("No feedback data available.");
        }

        // Compute statistics
        double totalRating = 0;
        int ratingCount = 0;
        int lowRatingCount = 0;
        int highRatingCount = 0;
        Map<String, Integer> feedbackTrends = new HashMap<>();
        Map<String, Integer> ratingDistribution = new HashMap<>();
        Map<LocalDate, Integer> ratingsOverTime = new HashMap<>();
    for (Feedback feedback : feedbackList) {
        Integer rating = feedback.getRating();
        if (rating == null) {
            continue; // Skip processing if rating is null
        }
    
        totalRating += rating;
        ratingCount++;
    
        // Categorize feedback trends (counting feedback text occurrences)
        String activity = feedback.getFeedbackText();
        feedbackTrends.put(activity, feedbackTrends.getOrDefault(activity, 0) + 1);
    
        // Count low & high ratings
        if (rating <= 2) {
            lowRatingCount++;
        } else {
            highRatingCount++;
        }
    
        // Rating distribution (1-star, 2-star, 3-star, etc.)
        ratingDistribution.put("" + rating, ratingDistribution.getOrDefault("" + rating, 0) + 1);
    
        // Group ratings by date
        LocalDate date = feedback.getCreatedAt().toLocalDate();
        ratingsOverTime.put(date, ratingsOverTime.getOrDefault(date, 0) + 1);
    }

        // Compute averages
        double averageRating = ratingCount == 0 ? 0 : totalRating / ratingCount;
        double lowRatingPercentage = (ratingCount == 0) ? 0 : ((double) lowRatingCount / ratingCount) * 100;

        // Prepare response
        Map<String, Object> report = new HashMap<>();
        report.put("averageRating", averageRating);
        report.put("lowRatingPercentage", lowRatingPercentage);
        report.put("feedbackTrends", feedbackTrends);
        report.put("ratingDistribution", ratingDistribution);
        report.put("ratingsOverTime", ratingsOverTime);

        return report;
    }
   
    
}
