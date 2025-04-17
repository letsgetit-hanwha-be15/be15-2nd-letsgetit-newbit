package com.newbit.column.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newbit.column.domain.Column;
import com.newbit.column.dto.response.GetColumnDetailResponseDto;
import com.newbit.column.dto.response.GetColumnListResponseDto;
import com.newbit.column.dto.response.GetMyColumnListResponseDto;
import com.newbit.column.mapper.ColumnMapper;
import com.newbit.column.repository.ColumnRepository;
import com.newbit.common.exception.BusinessException;
import com.newbit.common.exception.ErrorCode;
import com.newbit.purchase.query.service.ColumnPurchaseHistoryQueryService;
import com.newbit.user.service.MentorService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ColumnService {

    private final ColumnRepository columnRepository;
    private final ColumnPurchaseHistoryQueryService columnPurchaseHistoryQueryService;
    private final MentorService mentorService;
    private final ColumnMapper columnMapper;

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

    public List<GetMyColumnListResponseDto> getMyColumnList(Long userId) {
        Long mentorId = mentorService.getMentorIdByUserId(userId);

        List<Column> columns = columnRepository
                .findAllByMentorIdAndIsPublicTrueOrderByCreatedAtDesc(mentorId);

        return columns.stream()
                .map(columnMapper::toMyColumnListDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public Column getColumn(Long columnId) {
        return columnRepository.findById(columnId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COLUMN_NOT_FOUND));
    }
    
    @Transactional
    public void increaseLikeCount(Long columnId) {
        Column column = getColumn(columnId);
        column.increaseLikeCount();
        columnRepository.save(column);
    }

    @Transactional
    public void decreaseLikeCount(Long columnId) {
        Column column = getColumn(columnId);
        column.decreaseLikeCount();
        columnRepository.save(column);
    }
    
    @Transactional(readOnly = true)
    public String getColumnTitle(Long columnId) {
        Column column = getColumn(columnId);
        return column.getTitle();
    }
    
    @Transactional(readOnly = true)
    public boolean isColumnAuthor(Long columnId, Long userId) {
        Column column = getColumn(columnId);
        return isColumnAuthor(column, userId);
    }
    
    @Transactional(readOnly = true)
    public boolean isColumnAuthor(Column column, Long userId) {
        Long mentorId = column.getMentorId();
        Long authorId = getUserIdByMentorId(mentorId);
        return userId.equals(authorId);
    }
    
    @Transactional(readOnly = true)
    public Long getUserIdByMentorId(Long mentorId) {
        return mentorService.getUserIdByMentorId(mentorId);
    }
    
    @Transactional(readOnly = true)
    public Long getMentorIdByColumnId(Long columnId) {
        Column column = getColumn(columnId);
        return column.getMentorId();
    }
}
