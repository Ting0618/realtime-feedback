package com.feedback.realtime_feedback;

//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author tingwong
 */

//@Data // Generates getters, setters, toString, equals, hashCode
//@NoArgsConstructor // Generates no-args constructor
//@AllArgsConstructor // Generates all-args constructor
public class Feedback {
    private String id;
    private String userId;
    private String message;
    // 1-5
    private int rating;
    private LocalDateTime timestamp;

    // 1. No-argument constructor (from @NoArgsConstructor)
    public Feedback() {
    }

    // 2. All-arguments constructor (from @AllArgsConstructor)
    public Feedback(String id, String userId, String message, int rating, LocalDateTime timestamp) {
        this.id = id;
        this.userId = userId;
        this.message = message;
        this.rating = rating;
        this.timestamp = timestamp;
    }

    // Constructor function, automatically generate ID and timestamp
    public Feedback(String userId, String message, int rating) {
        this.id = UUID.randomUUID().toString();
        this.userId = userId;
        this.message = message;
        this.rating = rating;
        this.timestamp = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
