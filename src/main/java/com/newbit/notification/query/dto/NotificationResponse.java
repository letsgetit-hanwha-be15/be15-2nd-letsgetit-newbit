package com.newbit.notification.query.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationResponse {
    private Long notificationId;
    private String content;
    private String typeName;
    private Boolean isRead;
    private LocalDateTime createdAt;
}