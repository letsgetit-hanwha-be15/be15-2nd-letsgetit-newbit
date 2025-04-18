package com.newbit.newbitfeatureservice.notification.query.service;

import com.newbit.newbitfeatureservice.notification.query.dto.NotificationResponse;
import com.newbit.newbitfeatureservice.notification.query.mapper.NotificationQueryMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class NotificationQueryServiceTest {

    @InjectMocks
    private NotificationQueryService notificationQueryService;

    @Mock
    private NotificationQueryMapper notificationQueryMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getNotifications_returnsListOfNotifications() {
        // Given
        Long userId = 1L;
        NotificationResponse mockResponse = new NotificationResponse();
        mockResponse.setNotificationId(10L);
        mockResponse.setContent("새 댓글이 달렸습니다.");
        mockResponse.setTypeName("COMMENT");
        mockResponse.setIsRead(false);
        mockResponse.setCreatedAt(LocalDateTime.now());

        when(notificationQueryMapper.findAllByUserId(userId)).thenReturn(List.of(mockResponse));

        // When
        List<NotificationResponse> result = notificationQueryService.getNotifications(userId);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNotificationId()).isEqualTo(10L);
        verify(notificationQueryMapper, times(1)).findAllByUserId(userId);
    }
}