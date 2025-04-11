package com.newbit.notification.query.controller;

import com.newbit.auth.model.CustomUser;
import com.newbit.common.dto.ApiResponse;
import com.newbit.notification.command.application.dto.response.NotificationSendResponse;
import com.newbit.notification.query.dto.NotificationResponse;
import com.newbit.notification.query.service.NotificationQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "알림 API", description = "알림 목록 조회 API")
@RestController
@RequestMapping("/api/v1/notification")
@RequiredArgsConstructor
public class NotificationQueryController {

    private final NotificationQueryService notificationQueryService;

    @Operation(summary = "알림 목록 조회", description = "해당 유저의 알림 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getNotificationList(
            @AuthenticationPrincipal CustomUser customUser
    ) {
        List<NotificationResponse> notifications = notificationQueryService.getNotifications(customUser.getUserId());
        return ResponseEntity.ok(ApiResponse.success(notifications));
    }
}