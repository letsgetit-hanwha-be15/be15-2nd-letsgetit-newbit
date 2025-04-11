package com.newbit.notification.query.dto;

import com.newbit.notification.command.domain.aggregate.Notification;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public class NotificationResponse {
    private Long notificationId;
    private String content;
    private String typeName;
    private Boolean isRead;
    private LocalDateTime createdAt;

    public static NotificationResponse from(Notification notification) {
        return NotificationResponse.builder()
                .notificationId(notification.getNotificationId())
                .content(notification.getContent())
                .typeName(notification.getNotificationType().getName())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}