package com.example.demo.mq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RocketMQConsumerHelper {

    private static final Logger log = LoggerFactory.getLogger(RocketMQConsumerHelper.class);

    public <T> void handle(String topic, T message, java.util.function.Consumer<T> businessLogic) {
        if (null == message) {
            log.warn("Received null message from topic {}, skipping processing", topic);
            return;
        }
        if (null == businessLogic) {
            log.error("Business logic consumer is null for topic {}, cannot process message", topic);
            return;
        }
        try {
            businessLogic.accept(message);
        } catch (Exception e) {
            log.error("Failed to process message from topic {}: {}", topic, e.getMessage(), e);
        }
    }
}
