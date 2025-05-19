package com.feedback.realtime_feedback;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author tingwong
 */
@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private FeedbackStoreService feedbackStoreService;

    // DTO (Data Transfer Object) for creating feedback, to avoid exposing internal ID generation
    // For simplicity in this quick project, we can allow client to send full Feedback object
    // Or create a specific DTO like this:
    public static class CreateFeedbackRequest {
        public String userId;
        public String message;
        public int rating;
    }

    @PostMapping
    public ResponseEntity<String> submitFeedback(@RequestBody CreateFeedbackRequest request) {
        // Create a new Feedback object, ID and timestamp will be generated
        Feedback feedback = new Feedback(request.userId, request.message, request.rating);
        kafkaProducerService.sendFeedback(feedback);
        return ResponseEntity.status(HttpStatus.CREATED).body("Feedback submitted with ID: " + feedback.getId());
    }

    @GetMapping
    public ResponseEntity<List<Feedback>> getAllFeedback() {
        return ResponseEntity.ok(feedbackStoreService.getAllFeedback());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Feedback> getFeedbackById(@PathVariable String id) {
        Feedback feedback = feedbackStoreService.getFeedbackById(id);
        if (feedback != null) {
            return ResponseEntity.ok(feedback);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Feedback>> getFeedbackByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(feedbackStoreService.getFeedbackByUserId(userId));
    }
}
