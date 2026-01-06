package com.example.demo.mq.consumer;

import com.example.demo.mq.RocketMQConsumerHelper;
import com.example.demo.mq.constant.RocketMQConstant;
import com.example.demo.mq.event.dto.NotificationEvent;

import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RocketMQMessageListener(
        topic = RocketMQConstant.TOPIC_NOTIFICATION,
        consumerGroup = RocketMQConstant.DEMO_CONSUMER_GROUP_NOTIFICATION
)
public class NotificationConsumer implements RocketMQListener<NotificationEvent> {

    private static final Logger logger = LoggerFactory.getLogger(NotificationConsumer.class);

    private final RocketMQConsumerHelper rocketMQConsumerHelper ;

    @Autowired
    public NotificationConsumer(RocketMQConsumerHelper rocketMQConsumerHelper) {
        this.rocketMQConsumerHelper = rocketMQConsumerHelper;
    }

    @Override
    public void onMessage(NotificationEvent notificationEvent) {
        rocketMQConsumerHelper.handle(
                RocketMQConstant.TOPIC_NOTIFICATION,
                notificationEvent, msg -> {
                    logger.info("Received notification event: {}", msg);
                    // Add business logic processing here
                }
        );
    }
}
