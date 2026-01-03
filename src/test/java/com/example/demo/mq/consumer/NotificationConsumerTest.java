package com.example.demo.mq.consumer;

import com.example.demo.enums.NotificationType;
import com.example.demo.mq.RocketMQConsumerHelper;
import com.example.demo.mq.constant.RocketMQConstant;
import com.example.demo.mq.event.dto.NotificationEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class NotificationConsumerTest {

    @Mock
    private RocketMQConsumerHelper rocketMQConsumerHelper;

    @InjectMocks
    private NotificationConsumer notificationConsumer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void testOnMessage() {
        // 準備測試資料
        NotificationEvent event = new NotificationEvent();
        event.setId(1L);
        event.setContent("Test content");
        event.setType(NotificationType.email);
        event.setSubject("Test subject");
        event.setRecipient("test");
        event.setCreatedAt(new java.util.Date());


        // 模擬 rocketMQConsumerHelper.handle 方法
        doAnswer(invocation -> {
            String topic = invocation.getArgument(0);
            NotificationEvent msg = invocation.getArgument(1);
            Consumer<NotificationEvent> consumer = invocation.getArgument(2);

            // 執行 lambda
            consumer.accept(msg);
            return null;
        }).when(rocketMQConsumerHelper).handle(anyString(), any(NotificationEvent.class), any());

        // 呼叫測試方法
        notificationConsumer.onMessage(event);

        // 驗證 handle 被呼叫一次
        verify(rocketMQConsumerHelper, times(1))
                .handle(eq(RocketMQConstant.TOPIC_NOTIFICATION), eq(event), any());
    }

}
