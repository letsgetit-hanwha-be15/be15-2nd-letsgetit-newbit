package com.newbit.column.service;

import com.newbit.column.dto.response.GetColumnDetailResponseDto;
import com.newbit.column.dto.response.GetColumnListResponseDto;
import com.newbit.column.repository.ColumnRepository;
import com.newbit.common.exception.BusinessException;
import com.newbit.common.exception.ErrorCode;
import com.newbit.purchase.query.service.ColumnPurchaseHistoryQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ColumnService {

    private final ColumnRepository columnRepository;
    private final ColumnPurchaseHistoryQueryService columnPurchaseHistoryQueryService;

    public GetColumnDetailResponseDto getColumnDetail(Long userId, Long columnId) {
        GetColumnDetailResponseDto dto = columnRepository.findPublicColumnDetailById(columnId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COLUMN_NOT_FOUND));

        boolean isPurchased = columnPurchaseHistoryQueryService.hasUserPurchasedColumn(userId, columnId);
        if (!isPurchased) {
            throw new BusinessException(ErrorCode.COLUMN_NOT_PURCHASED);
        }

        return dto;
    }

    public Page<GetColumnListResponseDto> getPublicColumnList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return columnRepository.findAllByIsPublicTrueOrderByCreatedAtDesc(pageable);
    }
}
