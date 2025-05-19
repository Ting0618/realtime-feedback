package com.feedback.realtime_feedback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * @author tingwong
 */
@Service
public class KafkaProducerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);
    private static final String TOPIC_NAME = "feedback-topic";


    // Key: String (e.g., feedback ID), Value: Feedback object，这是 Spring Kafka 提供的模板类，用于发送消息到 Kafka
    @Autowired
    private KafkaTemplate<String, Feedback> kafkaTemplate;

    public void sendFeedback(Feedback feedback) {
        logger.info("Sending feedback to Kafka: {}", feedback);
        // 使用 feedback.getId() 作为消息的 Key，有助于 Kafka 分区和消息排序（如果分区策略基于 Key）
        // kafkaTemplate.send方法返回一个CompletableFuture<SendResult<K, V>> 对象，
        // .whenComplete(...) 只是在异步结束后（Kafka本身就是异步的），不管成功还是失败，补充一段代码去处理结果
        kafkaTemplate.send(TOPIC_NAME, feedback.getId(), feedback)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        logger.info("Sent message=[{}] with offset=[{}] to partition=[{}]",
                                feedback, result.getRecordMetadata().offset(), result.getRecordMetadata().partition());
                    } else {
                        logger.error("Unable to send message=[{}] due to : {}", feedback, ex.getMessage());
                    }
                });
    }
}
