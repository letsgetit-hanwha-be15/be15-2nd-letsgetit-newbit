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
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminColumnService {

    private final ColumnRequestRepository columnRequestRepository;
    private final AdminColumnMapper adminColumnMapper;

    @Transactional
    public AdminColumnResponseDto approveCreateColumnRequest(ApproveColumnRequestDto dto, Long adminUserId) {
        ColumnRequest request = columnRequestRepository.findById(dto.getColumnRequestId())
                .orElseThrow(() -> new BusinessException(ErrorCode.COLUMN_REQUEST_NOT_FOUND));

        if (request.getRequestType() != RequestType.CREATE) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST_TYPE);
        }

        request.approve(adminUserId);
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
        return adminColumnMapper.toDto(request);
    }
}
