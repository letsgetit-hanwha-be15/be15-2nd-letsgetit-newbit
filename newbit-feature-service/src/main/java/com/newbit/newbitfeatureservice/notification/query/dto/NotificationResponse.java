package com.newbit.newbitfeatureservice.notification.query.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "알림 응답 DTO")
public class NotificationResponse {

    @Schema(description = "알림 ID", example = "1")
    private Long notificationId;

    @Schema(description = "알림 내용", example = "새로운 댓글이 달렸습니다.")
    private String content;

    @Schema(description = "알림 유형 이름", example = "COMMENT")
    private String typeName;

    @Schema(description = "서비스 유형", example = "3")
    private Long serviceId;

    @Schema(description = "읽음 여부", example = "false")
    private Boolean isRead;

    @Schema(description = "알림 생성일시", example = "2025-04-11T17:35:20")
    private LocalDateTime createdAt;
}