package com.example.feed.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.feed.model.RatingSummary;
import com.example.feed.repository.RatingSummaryRepository;

@Service
public class RatingSummaryService {

    @Autowired
    private RatingSummaryRepository ratingSummaryRepository;

    public RatingSummary getRatingSummary() {
        return ratingSummaryRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Rating summary not found"));
    }
}
