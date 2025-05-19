package com.feedback.realtime_feedback;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

/**
 * 这个配置类做了三件事：
 * feedbackTopic(): 定义了一个 NewTopic bean。Spring Kafka 会在应用启动时（如果 Kafka Admin Client 配置正确且 Kafka Broker 允许）
 * 自动创建这个名为 feedback-topic 的主题。
 * feedbackConsumerFactory(): 为 Feedback 对象创建了一个自定义的 ConsumerFactory。
 * 这里关键是配置了 JsonDeserializer 并指定了 Feedback.class 以及信任的包。
 * feedbackKafkaListenerContainerFactory(): 基于上面的 ConsumerFactory 创建了一个监听器容器工厂。
 * 这个工厂的名字就是我们在 @KafkaListener 注解中引用的 feedbackKafkaListenerContainerFactory。
 * @author tingwong
 */

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    // 1. Topic Bean
    @Bean
    public NewTopic feedbackTopic() {
        return TopicBuilder.name("feedback-topic")
                .partitions(1)       // 简单起见，1个分区
                .replicas(1)         // 本地单节点 Kafka，1个副本
                .build();
    }

    // 2. Consumer Factory for Feedback objects
    @Bean
    public ConsumerFactory<String, Feedback> feedbackConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        // 使用 JsonDeserializer，并配置信任的包和默认类型
        JsonDeserializer<Feedback> deserializer = new JsonDeserializer<>(Feedback.class);
        deserializer.setRemoveTypeHeaders(false);
        // 信任包
        deserializer.addTrustedPackages("com.example.realtimefeedback");
        deserializer.setUseTypeMapperForKey(true);

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                // Key: String, Value: Feedback
                deserializer);
    }

    // 3. Kafka Listener Container Factory for Feedback objects
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Feedback> feedbackKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Feedback> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(feedbackConsumerFactory());
        return factory;
    }
}