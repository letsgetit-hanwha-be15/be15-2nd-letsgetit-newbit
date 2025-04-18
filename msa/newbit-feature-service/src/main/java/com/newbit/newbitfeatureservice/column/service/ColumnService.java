package com.newbit.newbitfeatureservice.column.service;

import java.util.List;

import com.newbit.newbitfeatureservice.client.user.MentorFeignClient;
import com.newbit.newbitfeatureservice.client.user.UserFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newbit.newbitfeatureservice.column.domain.Column;
import com.newbit.newbitfeatureservice.column.dto.response.GetColumnDetailResponseDto;
import com.newbit.newbitfeatureservice.column.dto.response.GetColumnListResponseDto;
import com.newbit.newbitfeatureservice.column.dto.response.GetMyColumnListResponseDto;
import com.newbit.newbitfeatureservice.column.mapper.ColumnMapper;
import com.newbit.newbitfeatureservice.column.repository.ColumnRepository;
import com.newbit.newbitfeatureservice.common.exception.BusinessException;
import com.newbit.newbitfeatureservice.common.exception.ErrorCode;
import com.newbit.newbitfeatureservice.purchase.query.service.ColumnPurchaseHistoryQueryService;

import lombok.RequiredArgsConstructor;

@Service
@Slf4j
@RequiredArgsConstructor
public class ColumnService {

    private final ColumnRepository columnRepository;
    private final ColumnPurchaseHistoryQueryService columnPurchaseHistoryQueryService;
    private final MentorFeignClient mentorFeignClient;
    private final UserFeignClient userFeignClient;
    private final ColumnMapper columnMapper;

    @Transactional(readOnly = true)
    public GetColumnDetailResponseDto getColumnDetail(Long userId, Long columnId) {
        GetColumnDetailResponseDto dto = columnRepository.findPublicColumnDetailById(columnId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COLUMN_NOT_FOUND));

        boolean isPurchased = columnPurchaseHistoryQueryService.hasUserPurchasedColumn(userId, columnId);
        if (!isPurchased) {
            throw new BusinessException(ErrorCode.COLUMN_NOT_PURCHASED);
        }
        Long mentorId = dto.getMentorId();
        Long authorUserId = mentorFeignClient.getUserIdByMentorId(mentorId).getData();
        String nickname = userFeignClient.getUserByUserId(authorUserId).getData().getNickname();
        dto.setMentorNickname(nickname);

        return dto;
    }

    @Transactional(readOnly = true)
    public Page<GetColumnListResponseDto> getPublicColumnList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<GetColumnListResponseDto> resultPage = columnRepository.findAllByIsPublicTrueOrderByCreatedAtDesc(pageable);
        List<GetColumnListResponseDto> content = resultPage.getContent();

        for (GetColumnListResponseDto dto : content) {
            try {
                Long mentorId = dto.getMentorId();

                // 1. 멘토 -> 유저
                var mentorResponse = mentorFeignClient.getUserIdByMentorId(mentorId);
                if (mentorResponse.getData() == null) continue;

                Long userId = mentorResponse.getData();

                // 2. 유저 -> 닉네임
                var userNickname = userFeignClient.getNicknameByUserId(userId).getData();
                if (userNickname == null) continue;
                log.debug("userResponse: {}", userNickname);

                dto.setMentorNickname(userNickname);

            } catch (Exception e) {
                log.warn("Failed to fetch mentor nickname for mentorId={}", dto.getMentorId(), e);
            }
        }

        return new PageImpl<>(content, pageable, resultPage.getTotalElements());
    }

    public List<GetMyColumnListResponseDto> getMyColumnList(Long userId) {
        Long mentorId = mentorFeignClient.getMentorIdByUserId(userId).getData();

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
        return mentorFeignClient.getUserIdByMentorId(mentorId).getData();
    }
    
    @Transactional(readOnly = true)
    public Long getMentorIdByColumnId(Long columnId) {
        Column column = getColumn(columnId);
        return column.getMentorId();
    }
}
