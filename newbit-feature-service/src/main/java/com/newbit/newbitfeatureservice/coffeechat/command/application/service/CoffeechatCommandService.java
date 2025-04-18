package com.newbit.newbitfeatureservice.coffeechat.command.application.service;

import com.newbit.newbitfeatureservice.client.user.MentorFeignClient;
import com.newbit.newbitfeatureservice.client.user.UserFeignClient;
import com.newbit.newbitfeatureservice.client.user.dto.MentorDTO;
import com.newbit.newbitfeatureservice.client.user.dto.UserDTO;
import com.newbit.newbitfeatureservice.coffeechat.command.application.dto.request.CoffeechatCancelRequest;
import com.newbit.newbitfeatureservice.coffeechat.command.domain.aggregate.RequestTime;
import com.newbit.newbitfeatureservice.coffeechat.command.domain.repository.CoffeechatRepository;
import com.newbit.newbitfeatureservice.coffeechat.command.application.dto.request.CoffeechatCreateRequest;
import com.newbit.newbitfeatureservice.coffeechat.command.domain.aggregate.Coffeechat;
import com.newbit.newbitfeatureservice.coffeechat.command.domain.repository.RequestTimeRepository;
import com.newbit.newbitfeatureservice.coffeechat.query.dto.request.CoffeechatSearchServiceRequest;
import com.newbit.newbitfeatureservice.coffeechat.query.dto.response.CoffeechatListResponse;
import com.newbit.newbitfeatureservice.coffeechat.query.service.CoffeechatQueryService;
import com.newbit.newbitfeatureservice.coffeechat.query.dto.response.ProgressStatus;
import com.newbit.newbitfeatureservice.coffeeletter.domain.chat.CoffeeLetterRoom;
import com.newbit.newbitfeatureservice.coffeeletter.dto.CoffeeLetterRoomDTO;
import com.newbit.newbitfeatureservice.coffeeletter.service.RoomService;
import com.newbit.newbitfeatureservice.common.exception.BusinessException;
import com.newbit.newbitfeatureservice.common.exception.ErrorCode;
import com.newbit.newbitfeatureservice.notification.command.application.dto.request.NotificationSendRequest;
import com.newbit.newbitfeatureservice.notification.command.application.service.NotificationCommandService;
import com.newbit.newbitfeatureservice.purchase.command.application.service.DiamondCoffeechatTransactionCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CoffeechatCommandService {

    private final CoffeechatRepository coffeechatRepository;
    private final CoffeechatQueryService coffeechatQueryService;
    private final RequestTimeRepository requestTimeRepository;
    private final MentorFeignClient mentorClient;
    private final DiamondCoffeechatTransactionCommandService transactionCommandService;
    private final NotificationCommandService notificationCommandService;
    private final RoomService roomService;
    private final UserFeignClient userClient;

    /**
     * 한두 번만 사용하는 간단한 조회여서 과도한 추상화를 피하기 위해
     * requestTimeService 로직 대신 repository 직접 호출해서 사용
     */

    /* 커피챗 등록 */
    @Transactional
    public Long createCoffeechat(Long userId, CoffeechatCreateRequest request) {
        // 1. 진행중인 커피챗이 존재
        CoffeechatSearchServiceRequest coffeechatSearchServiceRequest = new CoffeechatSearchServiceRequest();
        coffeechatSearchServiceRequest.setMenteeId(userId);
        coffeechatSearchServiceRequest.setMentorId(request.getMentorId());
        coffeechatSearchServiceRequest.setIsProgressing(true);
        CoffeechatListResponse coffeechatDtos = coffeechatQueryService.getCoffeechats(coffeechatSearchServiceRequest);
        if (!coffeechatDtos.getCoffeechats().isEmpty()) throw new BusinessException(ErrorCode.COFFEECHAT_ALREADY_EXIST);

        // 2. 커피챗 등록
        Coffeechat newCoffeechat = Coffeechat.of(userId, request.getMentorId(), request.getRequestMessage(), request.getPurchaseQuantity());

        Coffeechat coffeechat = coffeechatRepository.save(newCoffeechat);

        // 3. 커피챗 요청 등록(서비스 함수 생성)
        createRequestTime(coffeechat.getCoffeechatId(), request.getRequestTimes(), request.getPurchaseQuantity());

        // 4. 커피챗 요청 등록시 멘토에게 실시간 알림 발송
        notificationCommandService.sendNotification(
               new NotificationSendRequest(
                       mentorClient.getUserIdByMentorId(request.getMentorId()).getData()
                       , 3L
                       , coffeechat.getCoffeechatId()
                       , "새로운 커피챗 신청이 도착했습니다."
               )
        );

        return coffeechat.getCoffeechatId();
    }

    private void createRequestTime(Long coffeechatId, List<LocalDateTime> requestTimes, int purchaseQuantity) {
        requestTimes.forEach(time -> {
            if (time.isBefore(LocalDateTime.now())) {
                throw new BusinessException(ErrorCode.REQUEST_DATE_IN_PAST); // 시작 날짜가 오늘보다 이전입니다.
            }

            LocalDateTime endTime = time.plusMinutes(30L * purchaseQuantity);
            requestTimeRepository.save(RequestTime.of(
                    time.toLocalDate(),
                    time,
                    endTime,
                    coffeechatId
            ));
        });
    }


    @Transactional
    public void markAsPurchased(Long coffeechatId) {
        Coffeechat coffeechat = coffeechatRepository.findById(coffeechatId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COFFEECHAT_NOT_FOUND));

        if (coffeechat.getProgressStatus() != ProgressStatus.PAYMENT_WAITING) {
            throw new BusinessException(ErrorCode.COFFEECHAT_NOT_PURCHASABLE);
        }

        coffeechat.markAsPurchased();
    }

    @Transactional
    public void acceptCoffeechatTime(Long requestTimeId) {
        // 1. requestTime 객체 찾기
        RequestTime requestTime = requestTimeRepository.findById(requestTimeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REQUEST_TIME_NOT_FOUND));

        // 2. 커피챗 ID로 커피챗 객체 찾기
        Coffeechat coffeechat = coffeechatRepository.findById(requestTime.getCoffeechatId())
                .orElseThrow(() -> new BusinessException(ErrorCode.COFFEECHAT_NOT_FOUND));

        // 3. 커피챗 객체 update하기
        coffeechat.confirmSchedule(requestTime.getStartTime());

        // 4. 채팅방 열기
        Long mentorUserId = mentorClient.getUserIdByMentorId(coffeechat.getMentorId()).getData();
        UserDTO mentor = userClient.getUserByUserId(mentorUserId).getData();
        UserDTO mentee = userClient.getUserByUserId(coffeechat.getMenteeId()).getData();
        CoffeeLetterRoomDTO roomDto = CoffeeLetterRoomDTO.builder()
                .coffeeChatId(coffeechat.getCoffeechatId())
                .mentorId(coffeechat.getMentorId())
                .mentorName(mentor.getNickname())
                .menteeId(coffeechat.getMenteeId())
                .menteeName(mentee.getNickname())
                .createdAt(requestTime.getStartTime())
                .endTime(requestTime.getEndTime())
                .status(CoffeeLetterRoom.RoomStatus.ACTIVE)
                .build();
        roomService.createRoom(roomDto);

        // 5. 해당 coffeechatId에 대한 requestTime 객체 리스트 찾기
        List<RequestTime> requests = requestTimeRepository.findAllByCoffeechatId(coffeechat.getCoffeechatId());

        // 6. 해당 객체들 삭제
        requests.forEach(req -> requestTimeRepository.deleteById(req.getRequestTimeId()));

        // 7. 멘티에게 승인 알림 보내주기
        notificationCommandService.sendNotification(
                new NotificationSendRequest(
                        coffeechat.getMenteeId()
                        , 4L
                        , coffeechat.getCoffeechatId()
                        , "커피챗 요청이 승인되었습니다."
                )
        );
    }

    @Transactional
    public void rejectCoffeechatTime(Long coffeechatId) {
        // 1. 커피챗 ID로 커피챗 객체 찾기
        Coffeechat coffeechat = coffeechatRepository.findById(coffeechatId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COFFEECHAT_NOT_FOUND));

        // 2. 커피챗 객체 update하기
        coffeechat.rejectSchedule();

        // 3. 해당 coffeechatId에 대한 requestTime 객체 리스트 찾기
        List<RequestTime> requests = requestTimeRepository.findAllByCoffeechatId(coffeechatId);

        // 4. 해당 객체들 삭제
        requests.forEach(req -> requestTimeRepository.deleteById(req.getRequestTimeId()));

        // 5. 멘토에게 거절 알림 보내주기
        notificationCommandService.sendNotification(
                new NotificationSendRequest(
                        coffeechat.getMenteeId()
                        , 5L
                        , coffeechat.getCoffeechatId()
                        , "커피챗 요청이 거절되었습니다."
                )
        );
    }

    @Transactional
    public void closeCoffeechat(Long coffeechatId) {
        // 1. 커피챗 ID로 커피챗 객체 찾기
        Coffeechat coffeechat = coffeechatRepository.findById(coffeechatId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COFFEECHAT_NOT_FOUND));

        // 2. 커피챗 객체 update하기
        coffeechat.closeSchedule();
    }

    @Transactional
    public void confirmPurchaseCoffeechat(Long coffeechatId) {
        // 1. 커피챗 ID로 커피챗 객체 찾기
        Coffeechat coffeechat = coffeechatRepository.findById(coffeechatId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COFFEECHAT_NOT_FOUND));

        // 2. 커피챗 객체 update하기
        coffeechat.confirmPurchaseSchedule();

        // 3. 멘토ID로 멘토 객체 가져오기
        MentorDTO mentorDTO = mentorClient.getMentorInfo(coffeechat.getMentorId()).getData();

        // 4. 정산내역에 추가하기
        int totalQuantity = mentorDTO.getPrice() * coffeechat.getPurchaseQuantity();
        transactionCommandService.addSaleHistory(coffeechat.getMentorId(), totalQuantity, coffeechatId);
    }

    @Transactional
    public void cancelCoffeechat(Long userId, CoffeechatCancelRequest coffeechatCancelRequest) {
        // 1. 커피챗 ID로 커피챗 객체 찾기
        Coffeechat coffeechat = coffeechatRepository.findById(coffeechatCancelRequest.getCoffeechatId())
                .orElseThrow(() -> new BusinessException(ErrorCode.COFFEECHAT_NOT_FOUND));

        // 2. userId가 멘티Id인지 확인하기
        if(!coffeechat.getMenteeId().equals(userId)) {
            throw new BusinessException(ErrorCode.COFFEECHAT_CANCEL_NOT_ALLOWED);
        }

        // 3. 커피챗이 CANCEL 상태이거나, COMPLETE 상태이면 에러
        switch (coffeechat.getProgressStatus()){
            case CANCEL, COMPLETE -> throw new BusinessException(ErrorCode.INVALID_COFFEECHAT_STATUS_CANCEL);
        }

        // 4. 커피챗이 coffeechat_waiting 상태이면 환불 진행하기
        if(coffeechat.getProgressStatus().equals(ProgressStatus.COFFEECHAT_WAITING)) {
            MentorDTO mentorDTO = mentorClient.getMentorInfo(coffeechat.getMentorId()).getData();
            int totalQuantity = mentorDTO.getPrice() * coffeechat.getPurchaseQuantity();
            transactionCommandService.refundCoffeeChat(coffeechatCancelRequest.getCoffeechatId(), userId, totalQuantity);
        }

        // 5. 커피챗 객체 업데이트하기
        coffeechat.cancelCoffeechat(coffeechatCancelRequest.getCancelReasonId());

        // 6. 채팅방 취소
        String roomId = roomService.findRoomIdByCoffeeChatId(coffeechat.getCoffeechatId());
        roomService.cancelRoom(roomId);

        // 7. 멘토에게 커피챗 취소 알림
        notificationCommandService.sendNotification(
                new NotificationSendRequest(
                        mentorClient.getUserIdByMentorId(coffeechat.getMentorId()).getData()
                        , 6L
                        , coffeechat.getCoffeechatId()
                        , "진행 예정인 커피챗이 취소되었습니다."
                )
        );
    }
}
