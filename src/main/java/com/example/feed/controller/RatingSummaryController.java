package com.example.feed.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.feed.model.RatingSummary;
import com.example.feed.service.RatingSummaryService;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin("*")
public class RatingSummaryController {

    @Autowired
    private RatingSummaryService ratingSummaryService;

    @PostMapping("/rating-summary")
    public ResponseEntity<RatingSummary> getRatingSummary() {
        RatingSummary ratingSummary = ratingSummaryService.getRatingSummary();
        return ResponseEntity.ok(ratingSummary);
    }
}
