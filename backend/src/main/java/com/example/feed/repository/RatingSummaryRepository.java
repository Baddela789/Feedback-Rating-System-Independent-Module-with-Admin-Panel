package com.example.feed.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.feed.model.RatingSummary;

@Repository
public interface RatingSummaryRepository extends JpaRepository<RatingSummary, Long> {
}