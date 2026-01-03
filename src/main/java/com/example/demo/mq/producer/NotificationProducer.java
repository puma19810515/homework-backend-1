package com.example.demo.mq.producer;

import com.example.demo.model.Notification;
import com.example.demo.mq.constant.RocketMQConstant;
import com.example.demo.mq.event.dto.NotificationEvent;
import org.apache.rocketmq.logging.org.slf4j.Logger;
import org.apache.rocketmq.logging.org.slf4j.LoggerFactory;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NotificationProducer {

    private static Logger logger = LoggerFactory.getLogger(NotificationProducer.class);

    private final RocketMQTemplate rocketMQTemplate;

    @Autowired
    public NotificationProducer(RocketMQTemplate rocketMQTemplate) {
        this.rocketMQTemplate = rocketMQTemplate;
    }

    public void sendNotification(Notification notification) {
        if (notification == null) {
            logger.warn("notification is null, skip sending message");
            return;
        }
        NotificationEvent event = new NotificationEvent(
                notification.getId(),
                notification.getType(),
                notification.getRecipient(),
                notification.getSubject(),
                notification.getContent(),
                notification.getCreatedAt()
        );
        send(RocketMQConstant.TOPIC_NOTIFICATION, event);
    }

    private void send(String topic, NotificationEvent event) {
        if (rocketMQTemplate == null){
            logger.error("rocketMQTemplate is null, cannot send message");
            return;
        }
        rocketMQTemplate.convertAndSend(topic, event);
    }
}
