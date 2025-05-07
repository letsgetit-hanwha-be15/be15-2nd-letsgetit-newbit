package com.newbit.newbitfeatureservice.column.service;

import com.newbit.newbitfeatureservice.client.user.MentorFeignClient;
import com.newbit.newbitfeatureservice.client.user.UserFeignClient;
import com.newbit.newbitfeatureservice.column.domain.Series;
import com.newbit.newbitfeatureservice.column.dto.request.CreateColumnRequestDto;
import com.newbit.newbitfeatureservice.column.dto.request.DeleteColumnRequestDto;
import com.newbit.newbitfeatureservice.column.dto.request.UpdateColumnRequestDto;
import com.newbit.newbitfeatureservice.column.dto.response.*;
import com.newbit.newbitfeatureservice.column.domain.Column;
import com.newbit.newbitfeatureservice.column.domain.ColumnRequest;
import com.newbit.newbitfeatureservice.column.enums.RequestType;
import com.newbit.newbitfeatureservice.column.mapper.ColumnMapper;
import com.newbit.newbitfeatureservice.column.repository.ColumnRepository;
import com.newbit.newbitfeatureservice.column.repository.ColumnRequestRepository;
import com.newbit.newbitfeatureservice.column.repository.SeriesRepository;
import com.newbit.newbitfeatureservice.common.exception.BusinessException;
import com.newbit.newbitfeatureservice.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ColumnRequestService {

    private final ColumnRepository columnRepository;
    private final ColumnRequestRepository columnRequestRepository;
    private final SeriesRepository seriesRepository;
    private final MentorFeignClient mentorFeignClient;
    private final ColumnMapper columnMapper;
    private final UserFeignClient userFeignClient;

    public CreateColumnResponseDto createColumnRequest(CreateColumnRequestDto dto, Long userId) {
        // 1. Mentor 조회
        log.info("userId 테슽 {}", userId);
        Long mentorId = mentorFeignClient.getMentorIdByUserId(userId).getData();

        // 2. 시리즈 조회 (선택 사항이므로 null 가능성 처리)
        Series series = null;
        if (dto.getSeriesId() != null) {
            series = seriesRepository.findById(dto.getSeriesId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.SERIES_NOT_FOUND));
        }

        // 3. Column 저장
        Column column = columnMapper.toColumn(dto, mentorId, series);
        Column savedColumn = columnRepository.save(column);

        // 4. ColumnRequest 저장
        ColumnRequest request = columnMapper.toColumnRequest(dto, savedColumn);
        ColumnRequest savedRequest = columnRequestRepository.save(request);

        return CreateColumnResponseDto.builder()
                .columnRequestId(savedRequest.getColumnRequestId())
                .build();
    }

    public UpdateColumnResponseDto updateColumnRequest(UpdateColumnRequestDto dto, Long columnId, Long userId) {
        // 1. userId → mentorId 변환
        Long mentorId = mentorFeignClient.getMentorIdByUserId(userId).getData();

        // 2. columnId로 Column 조회
        Column column = columnRepository.findById(columnId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COLUMN_NOT_FOUND));

        // 3. 본인 칼럼인지 확인
        if (!column.getMentorId().equals(mentorId)) {
            throw new BusinessException(ErrorCode.COLUMN_NOT_OWNED);
        }

        // 4. 공개된 칼럼인지 확인
        if (!column.getIsPublic()) {
            throw new BusinessException(ErrorCode.COLUMN_NOT_OWNED);
        }

        // 5. 수정 요청 생성
        ColumnRequest request = ColumnRequest.builder()
                .requestType(RequestType.UPDATE)
                .isApproved(false)
                .updatedTitle(dto.getTitle())
                .updatedContent(dto.getContent())
                .updatedPrice(dto.getPrice())
                .updatedThumbnailUrl(dto.getThumbnailUrl())
                .column(column)
                .build();

        ColumnRequest saved = columnRequestRepository.save(request);

        return UpdateColumnResponseDto.builder()
                .columnRequestId(saved.getColumnRequestId())
                .build();
    }

    public DeleteColumnResponseDto deleteColumnRequest(DeleteColumnRequestDto dto, Long columnId) {
        Column column = columnRepository.findById(columnId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COLUMN_NOT_FOUND));

        ColumnRequest request = ColumnRequest.builder()
                .requestType(RequestType.DELETE)
                .isApproved(false)
                .column(column)
                .build();

        ColumnRequest saved = columnRequestRepository.save(request);

        return DeleteColumnResponseDto.builder()
                .columnRequestId(saved.getColumnRequestId())
                .build();
    }

    public Page<GetMyColumnRequestResponseDto> getMyColumnRequests(Long userId, Pageable pageable) {
        Long mentorId = mentorFeignClient.getMentorIdByUserId(userId).getData();

        Page<ColumnRequest> page = columnRequestRepository.findAllByColumn_MentorIdOrderByCreatedAtDesc(mentorId, pageable);

        return page.map(columnMapper::toMyColumnRequestResponseDto);
    }

    public Integer getColumnPriceById(Long columnId) {
        return columnRepository.findById(columnId)
                .map(Column::getPrice)
                .orElseThrow(() -> new BusinessException(ErrorCode.COLUMN_NOT_FOUND));
    }

    public Long getMentorId(Long columnId) {
        return columnRepository.findById(columnId)
                .map(Column::getMentorId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COLUMN_NOT_FOUND));
    }

    public Page<AdminColumnRequestResponseDto> getAllColumnRequests(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<ColumnRequest> columnRequestPage = columnRequestRepository.findAll(pageable);

        return columnRequestPage.map(request -> {
            Long mentorId = request.getColumn().getMentorId();
            Long userId = mentorFeignClient.getUserIdByMentorId(mentorId).getData();
            String nickname;
            try {
                nickname = userFeignClient.getNicknameByUserId(userId).getData();
            } catch (Exception e) {
                log.error("닉네임 조회 실패 - userId: {}", userId, e);
                nickname = "익명 멘토"; // fallback
            }

            return columnMapper.toAdminColumnRequestResponseDto(request, nickname);
        });
    }
}
