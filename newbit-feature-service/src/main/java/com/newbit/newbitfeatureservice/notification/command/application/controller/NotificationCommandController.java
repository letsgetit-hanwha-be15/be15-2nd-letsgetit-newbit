package com.newbit.newbitfeatureservice.notification.command.application.controller;


import com.newbit.newbitfeatureservice.common.dto.ApiResponse;
import com.newbit.newbitfeatureservice.notification.command.application.dto.request.NotificationSendRequest;
import com.newbit.newbitfeatureservice.notification.command.application.service.NotificationCommandService;
import com.newbit.newbitfeatureservice.notification.command.infrastructure.SseEmitterRepository;
import com.newbit.newbitfeatureservice.security.model.CustomUser;
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
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Tag(name = "알림 API", description = "알림 연결, 발송 API")
@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationCommandController {

    private final SseEmitterRepository sseEmitterRepository;
    private final NotificationCommandService notificationCommandService;


    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@AuthenticationPrincipal CustomUser customUser) {
        Long userId = customUser.getUserId();

        String emitterId = userId + "_" + UUID.randomUUID();
        SseEmitter emitter = new SseEmitter(60 * 60 * 1000L); // 1시간

        sseEmitterRepository.save(emitterId, userId, emitter);

        emitter.onCompletion(() -> sseEmitterRepository.deleteById(emitterId));
        emitter.onTimeout(() -> sseEmitterRepository.deleteById(emitterId));
        emitter.onError((e) -> sseEmitterRepository.deleteById(emitterId));

        try {
            emitter.send(SseEmitter.event().name("connect").data("SSE 연결 성공"));
        } catch (IOException e) {
            emitter.completeWithError(e);
        }

        // 🔁 30초마다 heartbeat 전송
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            try {
                emitter.send(SseEmitter.event().name("heartbeat").data("ping"));
            } catch (IOException e) {
                emitter.completeWithError(e);
            }
        }, 30, 30, TimeUnit.SECONDS); // 30초 후 시작, 30초 간격

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

    //    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @AuthenticationPrincipal CustomUser user,
            @PathVariable Long notificationId
    ) {
        notificationCommandService.markAsRead(user.getUserId(), notificationId);
        return ResponseEntity.ok().build();
    }

    //    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(@AuthenticationPrincipal CustomUser user) {
        System.out.println("user: " + user);
        notificationCommandService.markAllAsRead(user.getUserId());
        return ResponseEntity.ok(ApiResponse.success(null));
    }


}