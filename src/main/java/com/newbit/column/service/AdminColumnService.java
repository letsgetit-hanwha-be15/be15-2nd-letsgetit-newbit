package com.newbit.column.service;

import com.newbit.column.domain.Column;
import com.newbit.column.domain.ColumnRequest;
import com.newbit.column.dto.request.ApproveColumnRequestDto;
import com.newbit.column.dto.request.RejectColumnRequestDto;
import com.newbit.column.dto.response.AdminColumnResponseDto;
import com.newbit.column.enums.RequestType;
import com.newbit.column.mapper.AdminColumnMapper;
import com.newbit.column.repository.ColumnRequestRepository;
import com.newbit.common.exception.BusinessException;
import com.newbit.common.exception.ErrorCode;
import com.newbit.notification.command.application.dto.request.NotificationSendRequest;
import com.newbit.notification.command.application.service.NotificationCommandService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminColumnService {

    private final ColumnRequestRepository columnRequestRepository;
    private final AdminColumnMapper adminColumnMapper;
    private final NotificationCommandService notificationCommandService;

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

        notificationCommandService.sendNotification(
                new NotificationSendRequest(
                        request.getColumn().getMentor().getUser().getUserId()
                        , 11L
                        , request.getColumnRequestId(),
                        notificationContent
                )
        );

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

        notificationCommandService.sendNotification(
                new NotificationSendRequest(
                        request.getColumn().getMentor().getUser().getUserId()
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


        String notificationContent = String.format("'%s' 칼럼이 수정 승인되었습니다.",
                request.getUpdatedTitle());


        notificationCommandService.sendNotification(
                new NotificationSendRequest(
                        request.getColumn().getMentor().getUser().getUserId()
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


        String notificationContent = String.format("'%s' 칼럼이 수정이 거절되었습니다.",
                request.getColumn().getTitle());


        notificationCommandService.sendNotification(
                new NotificationSendRequest(
                        request.getColumn().getMentor().getUser().getUserId()
                        , 12L
                        , request.getColumnRequestId(),
                        notificationContent
                )
        );

        return adminColumnMapper.toDto(request);
    }
}
