package com.newbit.newbitfeatureservice.coffeechat.command.application.service;

import com.newbit.newbitfeatureservice.coffeechat.command.application.dto.request.CoffeechatCancelRequest;
import com.newbit.newbitfeatureservice.coffeechat.command.domain.aggregate.Coffeechat;
import com.newbit.newbitfeatureservice.coffeechat.query.dto.response.ProgressStatus;
import com.newbit.newbitfeatureservice.coffeeletter.service.RoomService;
import com.newbit.newbitfeatureservice.notification.command.application.dto.request.NotificationSendRequest;
import com.newbit.newbitfeatureservice.notification.command.application.service.NotificationCommandService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CoffeechatInternalServiceTest {

    @InjectMocks
    private CoffeechatInternalService internalService;

    @Mock
    private RoomService roomService;

    @Mock
    private NotificationCommandService notificationCommandService;

    @Test
    @DisplayName("cancelCoffeechatTransactional 성공 시 상태 업데이트, 방 취소, 알림 전송")
    void cancelCoffeechatTransactional_success() {
        // given
        Long mentorUserId = 10L;
        Long coffeechatId  = 20L;
        Long cancelReasonId = 1L;

        CoffeechatCancelRequest request = new CoffeechatCancelRequest(cancelReasonId);
        Coffeechat coffeechat = Coffeechat.of(5L, 6L, "테스트 메시지", 2);
        ReflectionTestUtils.setField(coffeechat, "coffeechatId", coffeechatId);

        when(roomService.findRoomIdByCoffeeChatId(coffeechatId))
                .thenReturn("room-123");

        // when
        internalService.cancelCoffeechatTransactional(request, coffeechat, mentorUserId);

        // then: 상태가 CANCEL로 변경
        assertEquals(ProgressStatus.CANCEL, coffeechat.getProgressStatus());

        // 채팅방 취소 호출 검증
        verify(roomService).findRoomIdByCoffeeChatId(coffeechatId);
        verify(roomService).cancelRoom("room-123");

        // 알림 전송 호출 검증
        verify(notificationCommandService).sendNotification(
                any(NotificationSendRequest.class)
        );
    }
}