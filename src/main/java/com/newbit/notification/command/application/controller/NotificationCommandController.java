package com.newbit.notification.command.application.controller;

import com.newbit.auth.model.CustomUser;
import com.newbit.common.dto.ApiResponse;
import com.newbit.notification.command.application.dto.request.NotificationSendRequest;
import com.newbit.notification.command.application.service.NotificationCommandService;
import com.newbit.notification.command.domain.repository.NotificationRepository;
import com.newbit.notification.command.infrastructure.SseEmitterRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.UUID;

@Tag(name = "알림 API", description = "알림 연결, 발송 API")
@RestController
@RequestMapping("/api/v1/notification")
@RequiredArgsConstructor
public class NotificationCommandController {

    private final SseEmitterRepository sseEmitterRepository;
    private final NotificationCommandService notificationCommandService;
    private final NotificationRepository notificationRepository;


    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@AuthenticationPrincipal CustomUser customUser) {
        Long userId = customUser.getUserId();

        String emitterId = userId + "_" + UUID.randomUUID();
        SseEmitter emitter = new SseEmitter(60 * 1000L); // 60초 타임아웃

        sseEmitterRepository.save(emitterId, userId, emitter);

        emitter.onCompletion(() -> sseEmitterRepository.deleteById(emitterId));
        emitter.onTimeout(() -> sseEmitterRepository.deleteById(emitterId));
        emitter.onError((e) -> sseEmitterRepository.deleteById(emitterId));

        try {
            emitter.send(SseEmitter.event().name("connect").data("SSE 연결 성공"));
        } catch (IOException e) {
            emitter.completeWithError(e);
        }

        return emitter;
    }

    @Operation(summary = "알림 전송", description = "특정 사용자에게 알림을 전송합니다.")
    @PostMapping("/send")
    public ResponseEntity<ApiResponse<Void>> sendNotification(
            @Valid @RequestBody NotificationSendRequest request
    ) {
        notificationCommandService.sendNotification(request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PatchMapping("/{notificationId}/read")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @AuthenticationPrincipal CustomUser user,
            @PathVariable Long notificationId
    ) {
        notificationCommandService.markAsRead(user.getUserId(), notificationId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/read-all")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> markAllAsRead(@AuthenticationPrincipal CustomUser user) {
        notificationCommandService.markAllAsRead(user.getUserId());
        return ResponseEntity.ok().build();
    }


}