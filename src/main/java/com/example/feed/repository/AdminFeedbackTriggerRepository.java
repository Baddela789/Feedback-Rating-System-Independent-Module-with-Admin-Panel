package com.example.feed.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.feed.model.AdminFeedbackTrigger;

@Repository
public interface AdminFeedbackTriggerRepository extends JpaRepository<AdminFeedbackTrigger, Long> {
}