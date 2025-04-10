package com.newbit.column.service;

import com.newbit.column.domain.Column;
import com.newbit.column.dto.response.GetColumnDetailResponseDto;
import com.newbit.column.dto.response.GetColumnListResponseDto;
import com.newbit.column.repository.ColumnRepository;
import com.newbit.common.exception.BusinessException;
import com.newbit.common.exception.ErrorCode;
import com.newbit.purchase.query.service.ColumnPurchaseHistoryQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ColumnService {

    private final ColumnRepository columnRepository;
    private final ColumnPurchaseHistoryQueryService columnPurchaseHistoryQueryService;

    public GetColumnDetailResponseDto getColumnDetail(Long userId, Long columnId) {
        Column column = columnRepository.findById(columnId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COLUMN_NOT_FOUND));

        if(!column.isPublic()) {
            throw new BusinessException(ErrorCode.COLUMN_NOT_FOUND);
        }

        boolean isPurchased = columnPurchaseHistoryQueryService.hasUserPurchasedColumn(userId, columnId);
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

    public List<GetColumnListResponseDto> getPublicColumnList() {
        List<Column> columns = columnRepository.findAllByIsPublicTrueOrderByCreatedAtDesc();

        return columns.stream()
                .map(column -> GetColumnListResponseDto.builder()
                        .columnId(column.getColumnId())
                        .title(column.getTitle())
                        .thumbnailUrl(column.getThumbnailUrl())
                        .price(column.getPrice())
                        .likeCount(column.getLikeCount())
                        .mentorId(column.getMentorId())
                        .build()
                ).collect(Collectors.toList());
    }
}
