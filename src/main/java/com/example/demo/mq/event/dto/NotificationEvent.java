package com.example.demo.mq.event.dto;

import com.example.demo.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationEvent {
    private Long id;
    private NotificationType type;
    private String recipient;
    private String subject;
    private String content;
    private Date createdAt;
}
