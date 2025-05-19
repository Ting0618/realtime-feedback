package com.feedback.realtime_feedback;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author tingwong
 * 在内存中存储反馈
 */
@Service
public class FeedbackStoreService {
    // 使用 ConcurrentHashMap 保证线程安全
    // Key: feedback ID, Value: Feedback object
    private final Map<String, Feedback> feedbackMap = new ConcurrentHashMap<>();

    public void storeFeedback(Feedback feedback) {
        feedbackMap.put(feedback.getId(), feedback);
    }

    public Feedback getFeedbackById(String id) {
        return feedbackMap.get(id);
    }

    public List<Feedback> getAllFeedback() {
        return new ArrayList<>(feedbackMap.values());
    }

    public List<Feedback> getFeedbackByUserId(String userId) {
        return feedbackMap.values().stream()
                .filter(feedback -> userId.equals(feedback.getUserId()))
                .collect(Collectors.toList());
    }
}
