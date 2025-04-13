package com.newbit.column.service;

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
    @Operation(summary = "칼럼 등록 요청 승인", description = "CREATE 타입의 칼럼 요청을 승인하고, 해당 칼럼을 공개 상태로 변경합니다.")
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
    @Operation(summary = "칼럼 등록 요청 거절", description = "CREATE 타입의 칼럼 요청을 거절하고, 거절 사유를 기록합니다.")
    public AdminColumnResponseDto rejectCreateColumnRequest(RejectColumnRequestDto dto, Long adminUserId) {
        ColumnRequest request = columnRequestRepository.findById(dto.getColumnRequestId())
                .orElseThrow(() -> new BusinessException(ErrorCode.COLUMN_REQUEST_NOT_FOUND));

        if (request.getRequestType() != RequestType.CREATE) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST_TYPE);
        }

        request.reject(dto.getReason(), adminUserId);
        return adminColumnMapper.toDto(request);
    }


}
