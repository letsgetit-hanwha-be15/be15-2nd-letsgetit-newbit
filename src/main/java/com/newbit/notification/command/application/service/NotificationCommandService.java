package com.newbit.notification.command.application.service;

import com.newbit.common.exception.BusinessException;
import com.newbit.common.exception.ErrorCode;
import com.newbit.notification.command.application.dto.response.NotificationSendResponse;
import com.newbit.notification.command.domain.aggregate.Notification;
import com.newbit.notification.command.domain.aggregate.NotificationType;
import com.newbit.notification.command.domain.repository.NotificationRepository;
import com.newbit.notification.command.domain.repository.NotificationTypeRepository;
import com.newbit.notification.command.application.dto.request.NotificationSendRequest;
import com.newbit.notification.command.infrastructure.SseEmitterRepository;
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
                .orElseThrow(() -> new BusinessException(ErrorCode.NOTIFICATION_TYPE_NOT_FOUND));

        // 2. 알림 생성 및 저장
        Notification notification = Notification.create(
                request.getUserId(),
                type,
                request.getContent()
        );
        notificationRepository.save(notification);

        // 3. 실시간 알림 전송
        NotificationSendResponse response = NotificationSendResponse.from(notification);
        sseEmitterRepository.send(request.getUserId(), response);
    }
}