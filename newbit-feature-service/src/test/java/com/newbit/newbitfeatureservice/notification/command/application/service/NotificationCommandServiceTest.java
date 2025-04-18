package com.newbit.newbitfeatureservice.notification.command.application.service;

import com.newbit.newbitfeatureservice.common.exception.BusinessException;
import com.newbit.newbitfeatureservice.common.exception.ErrorCode;
import com.newbit.newbitfeatureservice.notification.command.application.dto.request.NotificationSendRequest;
import com.newbit.newbitfeatureservice.notification.command.application.dto.response.NotificationSendResponse;
import com.newbit.newbitfeatureservice.notification.command.domain.aggregate.Notification;
import com.newbit.newbitfeatureservice.notification.command.domain.aggregate.NotificationType;
import com.newbit.newbitfeatureservice.notification.command.domain.repository.NotificationRepository;
import com.newbit.newbitfeatureservice.notification.command.domain.repository.NotificationTypeRepository;
import com.newbit.newbitfeatureservice.notification.command.infrastructure.SseEmitterRepository;
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
        Long serviceId = 20L;
        String content = "This is a test notification.";

        NotificationSendRequest request = new NotificationSendRequest(userId, typeId, serviceId, content);

        NotificationType type = NotificationType.builder()
                .id(typeId)
                .name("COLUMN_APPROVED")
                .build();

        Notification notification = Notification.create(userId, type, serviceId, content);

        when(notificationTypeRepository.findById(typeId)).thenReturn(Optional.of(type));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        // when
        notificationCommandService.sendNotification(request);

        // then
        verify(notificationTypeRepository, times(1)).findById(typeId);
        verify(notificationRepository, times(1)).save(any(Notification.class));
        verify(sseEmitterRepository, times(1)).send(eq(userId), any(NotificationSendResponse.class));
    }

    @Test
    void should_throwException_when_notificationTypeNotFound() {
        // given
        Long userId = 1L;
        Long invalidTypeId = 999L;
        Long serviceId = 20L;
        String content = "Alert";

        NotificationSendRequest request = new NotificationSendRequest(userId, invalidTypeId, serviceId, content);

        when(notificationTypeRepository.findById(invalidTypeId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> notificationCommandService.sendNotification(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.NOTIFICATION_TYPE_NOT_FOUND.getMessage());

        verify(notificationRepository, never()).save(any());
        verify(sseEmitterRepository, never()).send(any(), any());
    }

    @Test
    void markAsRead_success() {
        // given
        Long userId = 1L;
        Long serviceId = 10L;
        Long notificationId = 10L;
        NotificationType type = NotificationType.builder()
                .id(1L)
                .name("COMMENT")
                .build();

        Notification notification = Notification.create(userId, type, serviceId,"댓글이 달렸습니다");

        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));

        // when
        notificationCommandService.markAsRead(userId, notificationId);

        // then
        assert notification.getIsRead(); // 읽음 여부 확인
        verify(notificationRepository).findById(notificationId);
    }

    @Test
    void markAsRead_whenNotificationNotFound_thenThrow() {
        // given
        when(notificationRepository.findById(anyLong())).thenReturn(Optional.empty());

        // expect
        assertThatThrownBy(() -> notificationCommandService.markAsRead(1L, 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.NOTIFICATION_NOT_FOUND.getMessage());
    }

    @Test
    void markAsRead_whenAccessAnotherUsersNotification_thenThrow() {
        // given
        Long userId = 1L;
        Long otherUserId = 2L;
        Long serviceId = 10L;
        Long notificationId = 5L;
        NotificationType type = NotificationType.builder()
                .id(1L)
                .name("COMMENT")
                .build();

        Notification notification = Notification.create(otherUserId, type, serviceId, "다른 유저 알림");
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));

        // expect
        assertThatThrownBy(() -> notificationCommandService.markAsRead(userId, notificationId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.UNAUTHORIZED_ACCESS.getMessage());
    }

    @Test
    void markAllAsRead_success() {
        // given
        Long userId = 1L;

        // when
        notificationCommandService.markAllAsRead(userId);

        // then
        verify(notificationRepository, times(1)).markAllAsReadByUserId(userId);
    }
}