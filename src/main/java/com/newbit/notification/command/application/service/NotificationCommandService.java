package com.newbit.notification.command.application.service;

import com.newbit.notification.command.domain.aggregate.Notification;
import com.newbit.notification.command.domain.aggregate.NotificationType;
import com.newbit.notification.command.domain.repository.NotificationRepository;
import com.newbit.notification.command.domain.repository.NotificationTypeRepository;
import com.newbit.notification.command.application.dto.request.NotificationSendRequest;
import com.newbit.notification.command.infrastructure.SseEmitterRepository;
import com.newbit.notification.query.dto.NotificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationCommandService {

    private final NotificationRepository notificationRepository;
    private final NotificationTypeRepository notificationTypeRepository;
    private final SseEmitterRepository sseEmitterRepository;

    @Transactional
    public void sendNotification(NotificationSendRequest request) {
        // 1. 알림 타입 조회
        NotificationType type = notificationTypeRepository.findById(request.getNotificationTypeId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 알림 유형입니다."));

        // 2. 알림 생성 및 저장
        Notification notification = Notification.create(
                request.getUserId(),
                type,
                request.getContent()
        );
        notificationRepository.save(notification);

        // 3. 실시간 알림 전송
        NotificationResponse response = NotificationResponse.from(notification);
        sseEmitterRepository.send(request.getUserId(), response);
    }
}