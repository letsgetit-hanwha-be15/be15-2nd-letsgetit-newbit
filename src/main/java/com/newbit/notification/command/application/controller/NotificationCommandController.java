package com.newbit.notification.command.application.controller;

import com.newbit.auth.model.CustomUser;
import com.newbit.notification.command.infrastructure.SseEmitterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/notification")
@RequiredArgsConstructor
public class NotificationCommandController {

    private final SseEmitterRepository sseEmitterRepository;

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(
            @AuthenticationPrincipal CustomUser customUser
    ) {
        Long userId = customUser.getUserId();
        SseEmitter emitter = new SseEmitter(60 * 1000L);
        sseEmitterRepository.addEmitter(userId, emitter);

        emitter.onCompletion(() -> sseEmitterRepository.removeEmitter(userId));
        emitter.onTimeout(() -> sseEmitterRepository.removeEmitter(userId));
        emitter.onError((e) -> sseEmitterRepository.removeEmitter(userId));

        try {
            emitter.send(SseEmitter.event().name("connect").data("SSE 연결 성공"));
        } catch (IOException e) {
            emitter.completeWithError(e);
        }

        return emitter;
    }
}