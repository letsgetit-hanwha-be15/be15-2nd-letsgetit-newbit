package com.newbit.column.service;

import com.newbit.column.domain.Column;
import com.newbit.column.dto.response.GetColumnDetailResponseDto;
import com.newbit.column.repository.ColumnRepository;
import com.newbit.common.exception.BusinessException;
import com.newbit.common.exception.ErrorCode;
import com.newbit.purchase.command.domain.repository.ColumnPurchaseHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ColumnService {

    private final ColumnRepository columnRepository;
    private final ColumnPurchaseHistoryRepository columnPurchaseHistoryRepository;

    public GetColumnDetailResponseDto getColumnDetail(Long userId, Long columnId) {
        Column column = columnRepository.findById(columnId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COLUMN_NOT_FOUND));

        if(!column.isPublic()) {
            throw new BusinessException(ErrorCode.COLUMN_NOT_FOUND);
        }

        boolean isPurchased = columnPurchaseHistoryRepository.existsByUserIdAndColumnId(userId, columnId);
        if(!isPurchased) {
            throw new BusinessException(ErrorCode.COLUMN_NOT_PURCHASED);
        }

        return GetColumnDetailResponseDto.builder()
                .columnId(column.getColumnId())
                .title(column.getTitle())
                .content(column.getContent())
                .price(column.getPrice())
                .thumbnailUrl(column.getThumbnailUrl())
                .likeCount(column.getLikeCount())
                .mentorId(column.getMentorId())
                .build();
    }
}
