package com.newbit.newbitfeatureservice.column.service;

import com.newbit.newbitfeatureservice.client.user.MentorFeignClient;
import com.newbit.newbitfeatureservice.client.user.UserFeignClient;
import com.newbit.newbitfeatureservice.column.domain.Column;
import com.newbit.newbitfeatureservice.column.domain.ColumnRequest;
import com.newbit.newbitfeatureservice.column.dto.request.ApproveColumnRequestDto;
import com.newbit.newbitfeatureservice.column.dto.request.RejectColumnRequestDto;
import com.newbit.newbitfeatureservice.column.dto.response.AdminColumnResponseDto;
import com.newbit.newbitfeatureservice.column.enums.RequestType;
import com.newbit.newbitfeatureservice.column.mapper.AdminColumnMapper;
import com.newbit.newbitfeatureservice.column.repository.ColumnRequestRepository;
import com.newbit.newbitfeatureservice.common.exception.BusinessException;
import com.newbit.newbitfeatureservice.common.exception.ErrorCode;
import com.newbit.newbitfeatureservice.notification.command.application.dto.request.NotificationSendRequest;
import com.newbit.newbitfeatureservice.notification.command.application.service.NotificationCommandService;
import com.newbit.newbitfeatureservice.subscription.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminColumnService {

    private final ColumnRequestRepository columnRequestRepository;
    private final AdminColumnMapper adminColumnMapper;
    private final NotificationCommandService notificationCommandService;
    private final SubscriptionService subscriptionService;
    private final MentorFeignClient mentorFeignClient;
    private final UserFeignClient userFeignClient;

    @Transactional
    public AdminColumnResponseDto approveCreateColumnRequest(ApproveColumnRequestDto dto, Long adminUserId) {
        ColumnRequest request = columnRequestRepository.findById(dto.getColumnRequestId())
                .orElseThrow(() -> new BusinessException(ErrorCode.COLUMN_REQUEST_NOT_FOUND));

        if (request.getRequestType() != RequestType.CREATE) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST_TYPE);
        }

        request.approve(adminUserId);

        String notificationContent = String.format("'%s' 칼럼 등록이 승인되었습니다.",
                request.getColumn().getTitle());

        Long mentorId = request.getColumn().getMentorId();
        Long userId = mentorFeignClient.getUserIdByMentorId(mentorId).getData();

        notificationCommandService.sendNotification(
                new NotificationSendRequest(
                        userId, 11L, request.getColumnRequestId(), notificationContent
                )
        );

        List<Long> subscriberIds = subscriptionService.getSeriesSubscribers(request.getColumn().getSeries().getSeriesId());

        for (Long subscriberId : subscriberIds) {
            notificationCommandService.sendNotification(
                    new NotificationSendRequest(
                            subscriberId,
                            9L, // 시리즈 새 칼럼 등록 알림 타입 ID
                            request.getColumn().getColumnId(),
                            String.format("'%s' 시리즈에 새로운 칼럼 '%s'이(가) 등록되었습니다.",
                                    request.getColumn().getSeries().getTitle(), request.getColumn().getTitle())
                        )
                );
        }


        return adminColumnMapper.toDto(request);
    }

    @Transactional
    public AdminColumnResponseDto rejectCreateColumnRequest(RejectColumnRequestDto dto, Long adminUserId) {
        ColumnRequest request = columnRequestRepository.findById(dto.getColumnRequestId())
                .orElseThrow(() -> new BusinessException(ErrorCode.COLUMN_REQUEST_NOT_FOUND));

        if (request.getRequestType() != RequestType.CREATE) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST_TYPE);
        }

        request.reject(dto.getReason(), adminUserId);

        String notificationContent = String.format("'%s' 칼럼 등록이 거절되었습니다.",
                request.getColumn().getTitle());

        Long mentorId = request.getColumn().getMentorId();
        Long userId = mentorFeignClient.getUserIdByMentorId(mentorId).getData();

        notificationCommandService.sendNotification(
                new NotificationSendRequest(
                        userId
                        , 12L
                        , request.getColumnRequestId(),
                        notificationContent
                )
        );

        return adminColumnMapper.toDto(request);
    }

    @Transactional
    public AdminColumnResponseDto approveUpdateColumnRequest(ApproveColumnRequestDto dto, Long adminUserId) {
        ColumnRequest request = columnRequestRepository.findById(dto.getColumnRequestId())
                .orElseThrow(() -> new BusinessException(ErrorCode.COLUMN_REQUEST_NOT_FOUND));

        if (request.getRequestType() != RequestType.UPDATE) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST_TYPE);
        }

        // 칼럼 정보 업데이트
        Column column = request.getColumn();
        column.updateContent(request.getUpdatedTitle(), request.getUpdatedContent(), request.getUpdatedPrice(), request.getUpdatedThumbnailUrl());

        // 승인 처리
        request.approve(adminUserId);


        String notificationContent = String.format("'%s' 칼럼 수정이 승인되었습니다.",
                request.getUpdatedTitle());


        Long mentorId = request.getColumn().getMentorId();
        Long userId = mentorFeignClient.getUserIdByMentorId(mentorId).getData();
        notificationCommandService.sendNotification(
                new NotificationSendRequest(
                        userId
                        , 11L
                        , request.getColumnRequestId(),
                        notificationContent
                )
        );

        return adminColumnMapper.toDto(request);
    }

    @Transactional
    public AdminColumnResponseDto rejectUpdateColumnRequest(RejectColumnRequestDto dto, Long adminUserId) {
        ColumnRequest request = columnRequestRepository.findById(dto.getColumnRequestId())
                .orElseThrow(() -> new BusinessException(ErrorCode.COLUMN_REQUEST_NOT_FOUND));

        if (request.getRequestType() != RequestType.UPDATE) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST_TYPE);
        }

        request.reject(dto.getReason(), adminUserId);


        String notificationContent = String.format("'%s' 칼럼 수정이 거절되었습니다.",
                request.getColumn().getTitle());

        Long mentorId = request.getColumn().getMentorId();
        Long userId = mentorFeignClient.getUserIdByMentorId(mentorId).getData();

        notificationCommandService.sendNotification(
                new NotificationSendRequest(
                        userId
                        , 12L
                        , request.getColumnRequestId(),
                        notificationContent
                )
        );

        return adminColumnMapper.toDto(request);
    }

    @Transactional
    public AdminColumnResponseDto approveDeleteColumnRequest(ApproveColumnRequestDto dto, Long adminUserId) {
        ColumnRequest request = columnRequestRepository.findById(dto.getColumnRequestId())
                .orElseThrow(() -> new BusinessException(ErrorCode.COLUMN_REQUEST_NOT_FOUND));

        if (request.getRequestType() != RequestType.DELETE) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST_TYPE);
        }

        // 삭제 처리
        Column column = request.getColumn();
        column.markAsDeleted();

        // 승인 처리
        request.approve(adminUserId);

        String notificationContent = String.format("'%s' 칼럼 삭제가 승인되었습니다.",
                request.getColumn().getTitle());

        Long mentorId = request.getColumn().getMentorId();
        Long userId = mentorFeignClient.getUserIdByMentorId(mentorId).getData();

        notificationCommandService.sendNotification(
                new NotificationSendRequest(
                        userId
                        , 11L
                        , request.getColumnRequestId(),
                        notificationContent
                )
        );

        return adminColumnMapper.toDto(request);
    }

    @Transactional
    public AdminColumnResponseDto rejectDeleteColumnRequest(RejectColumnRequestDto dto, Long adminUserId) {
        ColumnRequest request = columnRequestRepository.findById(dto.getColumnRequestId())
                .orElseThrow(() -> new BusinessException(ErrorCode.COLUMN_REQUEST_NOT_FOUND));

        if (request.getRequestType() != RequestType.DELETE) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST_TYPE);
        }

        request.reject(dto.getReason(), adminUserId);

        String notificationContent = String.format("'%s' 칼럼 삭제가 거절되었습니다.",
                request.getColumn().getTitle());

        Long mentorId = request.getColumn().getMentorId();
        Long userId = mentorFeignClient.getUserIdByMentorId(mentorId).getData();

        notificationCommandService.sendNotification(
                new NotificationSendRequest(
                        userId
                        , 12L
                        , request.getColumnRequestId(),
                        notificationContent
                )
        );

        return adminColumnMapper.toDto(request);
    }

}
