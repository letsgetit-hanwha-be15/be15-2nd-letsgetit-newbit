package com.newbit.newbitfeatureservice.notification.command.application.dto.response;

import com.newbit.newbitfeatureservice.notification.command.domain.aggregate.Notification;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "알림 전송 응답 DTO")
public class NotificationSendResponse {

    @Schema(description = "알림 ID", example = "101")
    private Long notificationId;

    @Schema(description = "알림 내용", example = "멘토가 커피챗 요청을 수락했습니다.")
    private String content;

    @Schema(description = "알림 유형 이름", example = "COFFEECHAT_ACCEPTED")
    private String typeName;

    @Schema(description = "서비스 유형", example = "3")
    private Long serviceId;

    @Schema(description = "읽음 여부", example = "false")
    private Boolean isRead;

    @Schema(description = "알림 생성일시", example = "2025-04-11T18:45:00")
    private LocalDateTime createdAt;

    public static NotificationSendResponse from(Notification notification) {
        return NotificationSendResponse.builder()
                .notificationId(notification.getNotificationId())
                .content(notification.getContent())
                .serviceId(notification.getServiceId())
                .typeName(notification.getNotificationType().getName())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}