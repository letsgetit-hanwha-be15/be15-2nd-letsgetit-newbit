package com.newbit.newbitfeatureservice.notification.command.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
@Schema(description = "알림 전송 요청 DTO")
public class NotificationSendRequest {

    @NotNull
    @Schema(description = "알림을 받을 유저 ID", example = "1")
    private Long userId;

    @NotNull
    @Schema(description = "알림 유형 ID", example = "2")
    private Long notificationTypeId;

    @Schema(description = "서비스 ID", example = "3")
    private Long serviceId;

    @NotBlank
    @Schema(description = "알림 내용", example = "새로운 커피챗 신청이 도착했습니다.")
    private String content;
}