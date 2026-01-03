package com.example.demo.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum NotificationType {
    email, sms;

    @JsonCreator
    public static NotificationType fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Notification type cannot be null or blank");
        }

        try {
            return NotificationType.valueOf(value.toLowerCase());
        } catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Invalid notification type: " + value);
        }
    }
}
