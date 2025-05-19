package com.feedback.realtime_feedback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * @author tingwong
 */

@Service
public class KafkaConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);
    private static final String TOPIC_NAME = "feedback-topic";
    private static final String GROUP_ID = "feedback-group";

    @Autowired
    private FeedbackStoreService feedbackStoreService;

    // valueDeserializer 会将 JSON 字符串自动转换为 Feedback 对象
    @KafkaListener(topics = TOPIC_NAME, groupId = GROUP_ID,
            containerFactory = "feedbackKafkaListenerContainerFactory") // 指定特定的containerFactory
    public void consumeFeedback(Feedback feedback) {
        logger.info("Consumed feedback from Kafka: {}", feedback);
        feedbackStoreService.storeFeedback(feedback);
        logger.info("Stored feedback: {}", feedback.getId());
    }
}
