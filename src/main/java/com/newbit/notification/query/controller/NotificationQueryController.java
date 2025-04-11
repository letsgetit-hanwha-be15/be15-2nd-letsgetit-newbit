package com.newbit.notification.query.controller;


import com.newbit.auth.model.CustomUser;
import com.newbit.common.dto.ApiResponse;
import com.newbit.notification.query.dto.NotificationListResponse;
import com.newbit.notification.query.service.NotificationQueryService;
import com.newbit.purchase.query.dto.request.HistoryRequest;
import com.newbit.purchase.query.dto.response.ColumnPurchaseHistoryListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "알림 API", description = "알림 목록 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notification")
public class NotificationQueryController {

    NotificationQueryService notificationQueryService;

    @Operation(summary = "알림 목록 조회", description = "특정 유저의 알림 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<NotificationListResponse>> getNotificationList(
            @AuthenticationPrincipal CustomUser customUser) {
        NotificationListResponse response = notificationQueryService.getNotifications(customUser.getUserId());

        return ResponseEntity.ok(ApiResponse.success(response));
    }

}
