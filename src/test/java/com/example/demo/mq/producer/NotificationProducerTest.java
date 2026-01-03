package com.example.demo.mq.producer;


import com.example.demo.enums.NotificationType;
import com.example.demo.model.Notification;
import com.example.demo.mq.constant.RocketMQConstant;
import com.example.demo.mq.event.dto.NotificationEvent;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationProducerTest {

    @Mock
    private RocketMQTemplate rocketMQTemplate;

    @InjectMocks
    private NotificationProducer notificationProducer;

    private Notification notification;

    @BeforeEach
    public void setUp() {
        notification = new Notification();
        notification.setId(1L);
        notification.setType(NotificationType.email);
        notification.setRecipient("pumaTest@gmail.com");
        notification.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
    }

    @Test
    void sendNotification_success(){
        // when
        notificationProducer.sendNotification(notification);

        // then
        ArgumentCaptor<NotificationEvent> captor =
                ArgumentCaptor.forClass(NotificationEvent.class);

        verify(rocketMQTemplate, times(1))
                .convertAndSend(eq(RocketMQConstant.TOPIC_NOTIFICATION), captor.capture());

        NotificationEvent event = captor.getValue();
        assertEquals(notification.getId(), event.getId());
        assertEquals(notification.getType(), event.getType());
        assertEquals(notification.getRecipient(), event.getRecipient());
        assertEquals(notification.getCreatedAt(), event.getCreatedAt());
    }


    @Test
    void sendNotification_null_notification_should_not_send() {
        // when
        notificationProducer.sendNotification(null);

        // then
        verify(rocketMQTemplate, never())
                .convertAndSend(anyString(), Optional.ofNullable(any()));
    }
}
