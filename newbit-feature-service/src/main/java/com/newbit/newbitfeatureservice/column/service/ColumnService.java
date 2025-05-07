package com.newbit.newbitfeatureservice.column.service;

import java.util.ArrayList;
import java.util.List;

import com.newbit.newbitfeatureservice.client.user.MentorFeignClient;
import com.newbit.newbitfeatureservice.client.user.UserFeignClient;
import com.newbit.newbitfeatureservice.column.dto.request.SearchCondition;
import feign.FeignException;
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
        Long mentorId = dto.getMentorId();
        Long authorUserId = mentorFeignClient.getUserIdByMentorId(mentorId).getData();
        String nickname = userFeignClient.getUserByUserId(authorUserId).getData().getNickname();
        dto.setMentorNickname(nickname);

        boolean isPurchased = columnPurchaseHistoryQueryService.hasUserPurchasedColumn(userId, columnId);
        if (!isPurchased && !(authorUserId.equals(userId)) && !(dto.getPrice() == 0)) {
            throw new BusinessException(ErrorCode.COLUMN_NOT_PURCHASED);
        }

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

    @Transactional(readOnly = true)
    public Page<GetMyColumnListResponseDto> getMyColumnList(Long userId, int page, int size) {
        Long mentorId = mentorFeignClient.getMentorIdByUserId(userId).getData();
        Pageable pageable = PageRequest.of(page, size);
        Page<Column> columnsPage = columnRepository
                .findAllByMentorIdAndIsPublicTrueOrderByCreatedAtDesc(mentorId, pageable);

        return columnsPage
                .map(columnMapper::toMyColumnListDto);
    }

    @Transactional(readOnly = true)
    public Page<GetColumnListResponseDto> getMentorColumnList(Long mentorId, int page, int size) {
        // 1. 멘토 존재 여부 확인 및 userId 가져오기
        Long userId;
        try {
            userId = mentorFeignClient.getUserIdByMentorId(mentorId).getData();
        } catch (FeignException.NotFound e) {
            throw new BusinessException(ErrorCode.MENTOR_NOT_FOUND);
        }

        // 2. 닉네임 조회
        String mentorNickname;
        try {
            mentorNickname = userFeignClient.getNicknameByUserId(userId).getData();
        } catch (FeignException.NotFound e) {
            throw new BusinessException(ErrorCode.USER_INFO_NOT_FOUND);
        }

        // 3. 칼럼 페이지 조회
        Pageable pageable = PageRequest.of(page, size);
        Page<Column> columnsPage = columnRepository
                .findAllByMentorIdAndIsPublicTrueOrderByCreatedAtDesc(mentorId, pageable);

        // 4. DTO 매핑 + 닉네임 수동 주입
        return columnsPage.map(column -> {
            GetColumnListResponseDto dto = new GetColumnListResponseDto(
                    column.getColumnId(),
                    column.getTitle(),
                    column.getThumbnailUrl(),
                    column.getPrice(),
                    column.getLikeCount(),
                    mentorId,
                    column.getCreatedAt()
            );
            dto.setMentorNickname(mentorNickname);
            return dto;
        });
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

    @Transactional(readOnly = true)
    public Page<GetColumnListResponseDto> searchPublicColumns(SearchCondition condition, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        String keyword = condition.getKeyword();

        // 1차로 모든 공개 칼럼을 가져옴 (검색은 자바에서 처리)
        Page<GetColumnListResponseDto> resultPage = columnRepository.findAllByIsPublicTrueOrderByCreatedAtDesc(pageable);
        List<GetColumnListResponseDto> content = resultPage.getContent();

        List<GetColumnListResponseDto> filtered = new ArrayList<>();

        String loweredKeyword = keyword != null ? keyword.toLowerCase() : null;

        for (GetColumnListResponseDto dto : content) {
            try {
                Long mentorId = dto.getMentorId();
                Long userId = mentorFeignClient.getUserIdByMentorId(mentorId).getData();
                String nickname = userFeignClient.getNicknameByUserId(userId).getData();
                dto.setMentorNickname(nickname);

                if (loweredKeyword == null ||
                        (nickname != null && nickname.toLowerCase().contains(loweredKeyword)) ||
                        (dto.getTitle() != null && dto.getTitle().toLowerCase().contains(loweredKeyword))) {
                    filtered.add(dto);
                }

            } catch (Exception e) {
                log.warn("닉네임 조회 실패 mentorId={}", dto.getMentorId(), e);
            }
        }

        return new PageImpl<>(filtered, pageable, filtered.size());
    }


}
