package com.newbit.notification.command.application.dto.response;

import com.newbit.notification.command.domain.aggregate.Notification;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public class NotificationSendResponse {
    private Long notificationId;
    private String content;
    private String typeName;
    private Boolean isRead;
    private LocalDateTime createdAt;

    public static NotificationSendResponse from(Notification notification) {
        return NotificationSendResponse.builder()
                .notificationId(notification.getNotificationId())
                .content(notification.getContent())
                .typeName(notification.getNotificationType().getName())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}