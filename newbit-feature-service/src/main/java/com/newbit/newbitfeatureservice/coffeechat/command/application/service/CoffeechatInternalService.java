package com.newbit.newbitfeatureservice.coffeechat.command.application.service;

import com.newbit.newbitfeatureservice.coffeechat.command.application.dto.request.CoffeechatCancelRequest;
import com.newbit.newbitfeatureservice.coffeechat.command.domain.aggregate.Coffeechat;
import com.newbit.newbitfeatureservice.coffeeletter.service.RoomService;
import com.newbit.newbitfeatureservice.notification.command.application.dto.request.NotificationSendRequest;
import com.newbit.newbitfeatureservice.notification.command.application.service.NotificationCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CoffeechatInternalService {

    private final RoomService roomService;
    private final NotificationCommandService notificationCommandService;

    @Transactional
    protected void cancelCoffeechatTransactional(CoffeechatCancelRequest request,
                                                 Coffeechat coffeechat, Long mentorUserId) {
        // 1. 커피챗 상태 업데이트
        coffeechat.cancelCoffeechat(request.getCancelReasonId());

        // 2. 채팅방 취소 처리
        String roomId = roomService.findRoomIdByCoffeeChatId(coffeechat.getCoffeechatId());
        roomService.cancelRoom(roomId);

        // 3. 멘토에게 커피챗 취소 알림 전송
        notificationCommandService.sendNotification(
                new NotificationSendRequest(
                        mentorUserId,
                        6L, // 알림 타입 ID
                        coffeechat.getCoffeechatId(),
                        "진행 예정인 커피챗이 취소되었습니다."
                )
        );
    }
}
