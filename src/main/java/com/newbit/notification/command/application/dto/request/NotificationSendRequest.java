package com.newbit.notification.command.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class NotificationSendRequest {

    @NotNull
    private Long userId;

    @NotNull
    private Long notificationTypeId;

    @NotBlank
    private String content;
}