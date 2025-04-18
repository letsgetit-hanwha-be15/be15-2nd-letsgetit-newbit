package com.newbit.newbitfeatureservice.notification.command.infrastructure;

import com.newbit.newbitfeatureservice.notification.command.application.dto.response.NotificationSendResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Repository
public class SseEmitterRepository {

    // key: emitterId (userId_UUID), value: SseEmitter
    private final Map<String, SseEmitter> emitterMap = new ConcurrentHashMap<>();

    // key: userId, value: List<emitterId>
    private final Map<Long, List<String>> userEmitterMap = new ConcurrentHashMap<>();

    public void save(String emitterId, Long userId, SseEmitter emitter) {
        emitterMap.put(emitterId, emitter);
        userEmitterMap.computeIfAbsent(userId, id -> new ArrayList<>()).add(emitterId);
    }

    public void deleteById(String emitterId) {
        emitterMap.remove(emitterId);

        // userEmitterMap에서도 emitterId 제거
        userEmitterMap.forEach((userId, emitterIds) -> emitterIds.remove(emitterId));
    }

    public void send(Long userId, NotificationSendResponse response) {
        List<String> emitterIds = userEmitterMap.getOrDefault(userId, new ArrayList<>());

        for (String emitterId : emitterIds) {
            SseEmitter emitter = emitterMap.get(emitterId);
            if (emitter == null) continue;

            try {
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(response));
            } catch (IOException e) {
                log.warn("SSE 연결 실패 -> emitterId: {}, 이유: {}", emitterId, e.getMessage());
                emitter.completeWithError(e);
                deleteById(emitterId);
            }
        }
    }
}