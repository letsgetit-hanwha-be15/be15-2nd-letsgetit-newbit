package com.newbit.notification.command.application.service;

import com.newbit.common.exception.BusinessException;
import com.newbit.common.exception.ErrorCode;
import com.newbit.notification.command.application.dto.request.NotificationSendRequest;
import com.newbit.notification.command.application.dto.response.NotificationResponse;
import com.newbit.notification.command.domain.aggregate.Notification;
import com.newbit.notification.command.domain.aggregate.NotificationType;
import com.newbit.notification.command.domain.repository.NotificationRepository;
import com.newbit.notification.command.domain.repository.NotificationTypeRepository;
import com.newbit.notification.command.infrastructure.SseEmitterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class NotificationCommandServiceTest {

    @InjectMocks
    private NotificationCommandService notificationCommandService;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationTypeRepository notificationTypeRepository;

    @Mock
    private SseEmitterRepository sseEmitterRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void should_sendNotification_successfully_when_validRequest() {
        // given
        Long userId = 1L;
        Long typeId = 10L;
        String content = "This is a test notification.";

        NotificationSendRequest request = new NotificationSendRequest(userId, typeId, content);

        NotificationType type = NotificationType.builder()
                .id(typeId)
                .name("COLUMN_APPROVED")
                .build();

        Notification notification = Notification.create(userId, type, content);

        when(notificationTypeRepository.findById(typeId)).thenReturn(Optional.of(type));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        // when
        notificationCommandService.sendNotification(request);

        // then
        verify(notificationTypeRepository, times(1)).findById(typeId);
        verify(notificationRepository, times(1)).save(any(Notification.class));
        verify(sseEmitterRepository, times(1)).send(eq(userId), any(NotificationResponse.class));
    }

    @Test
    void should_throwException_when_notificationTypeNotFound() {
        // given
        Long userId = 1L;
        Long invalidTypeId = 999L;
        String content = "Alert";

        NotificationSendRequest request = new NotificationSendRequest(userId, invalidTypeId, content);

        when(notificationTypeRepository.findById(invalidTypeId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> notificationCommandService.sendNotification(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.NOTIFICATION_TYPE_NOT_FOUND.getMessage());

        verify(notificationRepository, never()).save(any());
        verify(sseEmitterRepository, never()).send(any(), any());
    }
}