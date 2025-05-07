package com.newbit.newbitfeatureservice.column.service;

import java.util.ArrayList;
import java.util.List;

import com.newbit.newbitfeatureservice.client.user.MentorFeignClient;
import com.newbit.newbitfeatureservice.client.user.UserFeignClient;
import com.newbit.newbitfeatureservice.column.dto.request.SearchCondition;
import feign.FeignException;
import com.newbit.newbitfeatureservice.column.dto.response.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newbit.newbitfeatureservice.column.domain.Column;
import com.newbit.newbitfeatureservice.column.domain.Series;
import com.newbit.newbitfeatureservice.column.dto.request.CreateSeriesRequestDto;
import com.newbit.newbitfeatureservice.column.dto.request.UpdateSeriesRequestDto;
import com.newbit.newbitfeatureservice.column.mapper.SeriesMapper;
import com.newbit.newbitfeatureservice.column.repository.ColumnRepository;
import com.newbit.newbitfeatureservice.column.repository.SeriesRepository;
import com.newbit.newbitfeatureservice.common.exception.BusinessException;
import com.newbit.newbitfeatureservice.common.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeriesService {

    private final SeriesRepository seriesRepository;
    private final ColumnRepository columnRepository;
    private final MentorFeignClient mentorFeignClient;
    private final SeriesMapper seriesMapper;
    private final UserFeignClient userFeignClient;

    @Transactional
    public CreateSeriesResponseDto createSeries(CreateSeriesRequestDto dto, Long userId) {
        // 1. 유저 → 멘토 엔티티 조회
        Long mentorId = mentorFeignClient.getMentorIdByUserId(userId).getData();

        // 2. 칼럼 ID에 해당하는 Column 리스트 조회
        List<Column> columns = (dto.getColumnIds() != null && !dto.getColumnIds().isEmpty())
                ? columnRepository.findAllById(dto.getColumnIds())
                :List.of(); // 비어있으면 빈 리스트로 대체

        if (columns.size() != dto.getColumnIds().size()) {
            throw new BusinessException(ErrorCode.COLUMN_NOT_FOUND);
        }

        // 3. (칼럼이 존재할 경우에만) 본인 칼럼인지 확인
        if(!columns.isEmpty()) {
            if (columns.size() != dto.getColumnIds().size()) {
                throw new BusinessException(ErrorCode.COLUMN_NOT_FOUND);
            }

            boolean hasInvalidOwner = columns.stream()
                    .anyMatch(column -> !column.getMentorId().equals(mentorId));
            if(hasInvalidOwner) {
                throw new BusinessException(ErrorCode.COLUMN_NOT_OWNED);
            }

            boolean hasAlreadyGrouped = columns.stream().anyMatch(column -> column.getSeries() != null);
            if (hasAlreadyGrouped) {
                throw new BusinessException(ErrorCode.COLUMN_ALREADY_IN_SERIES);
            }
        }

        // 4. 시리즈 저장
        Series series = seriesRepository.save(seriesMapper.toSeries(dto));

        // 5. (칼럼이 있을 경우에만) 각 칼럼에 시리즈 연결
        if (!columns.isEmpty()) {
            columns.forEach(column -> column.updateSeries(series));
            columnRepository.saveAll(columns);
        }

        // 6. 응답 반환
        return seriesMapper.toCreateSeriesResponseDto(series);
    }

    @Transactional
    public UpdateSeriesResponseDto updateSeries(Long seriesId, UpdateSeriesRequestDto dto, Long userId) {
        // 1. 멘토 조회
        Long mentorId = mentorFeignClient.getMentorIdByUserId(userId).getData();

        // 2. 시리즈 조회
        Series series = seriesRepository.findById(seriesId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SERIES_NOT_FOUND));

        // 칼럼 ID 검증
        List<Column> columns = columnRepository.findAllById(dto.getColumnIds());

        if (columns.size() != dto.getColumnIds().size()) {
            throw new BusinessException(ErrorCode.COLUMN_NOT_FOUND);
        }

        boolean hasInvalidOwner = columns.stream()
                .anyMatch(column -> !column.getMentorId().equals(mentorId));

        if (hasInvalidOwner) {
            throw new BusinessException(ErrorCode.COLUMN_NOT_OWNED);
        }

        boolean hasAlreadyGrouped = columns.stream()
                .anyMatch(column -> column.getSeries() != null && !column.getSeries().getSeriesId().equals(seriesId));

        if (hasAlreadyGrouped) {
            throw new BusinessException(ErrorCode.COLUMN_ALREADY_IN_SERIES);
        }

        // 기존 칼럼들의 시리즈 해제
        List<Column> existing = columnRepository.findAllBySeries_SeriesId(seriesId);
        for (Column column : existing) {
            column.updateSeries(null);
        }

        // 새 칼럼들 시리즈 연결
        for (Column column : columns) {
            column.updateSeries(series);
        }

        // 시리즈 내용 업데이트
        series.update(dto.getTitle(), dto.getDescription(), dto.getThumbnailUrl());

        // 저장
        columnRepository.saveAll(existing);
        columnRepository.saveAll(columns);

        return seriesMapper.toUpdateSeriesResponseDto(series);
    }

    @Transactional
    public void deleteSeries(Long seriesId, Long userId) {
        Long mentorId = mentorFeignClient.getMentorIdByUserId(userId).getData();

        Series series = seriesRepository.findById(seriesId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SERIES_NOT_FOUND));

        // 본인의 시리즈인지 확인
        List<Column> columns = columnRepository.findAllBySeries_SeriesId(seriesId);

        boolean isOwner = columns.stream().allMatch(
                column -> column.getMentorId().equals(mentorId));

        if (!isOwner) {
            throw new BusinessException(ErrorCode.COLUMN_NOT_OWNED);
        }

        // 연결된 칼럼의 series 해제
        columns.forEach(column -> column.updateSeries(null));
        columnRepository.saveAll(columns);

        // 시리즈 삭제
        seriesRepository.delete(series);
    }

    @Transactional(readOnly = true)
    public Series getSeries(Long seriesId) {
        return seriesRepository.findById(seriesId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SERIES_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public GetSeriesDetailResponseDto getSeriesDetail(Long seriesId) {
        Series series = seriesRepository.findById(seriesId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SERIES_NOT_FOUND));

        return seriesMapper.toGetSeriesDetailResponseDto(series);
    }

    @Transactional(readOnly = true)
    public Page<GetSeriesListResponseDto> getPublicSeriesList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Series> allSeries = seriesRepository.findAllByColumnsIsNotEmpty(pageable);

        List<GetSeriesListResponseDto> content = new ArrayList<>();

        for (Series series : allSeries.getContent()) {
            try {
                Long mentorId = series.getMentorId();

                // 1단계: mentorId -> userId
                var mentorResponse = mentorFeignClient.getUserIdByMentorId(mentorId);
                if (mentorResponse.getData() == null) continue;

                Long userId = mentorResponse.getData();

                // 2단계: userId -> nickname
                var nickname = userFeignClient.getNicknameByUserId(userId).getData();
                if (nickname == null) continue;

                // DTO 생성 + 닉네임 주입
                GetSeriesListResponseDto dto = GetSeriesListResponseDto.builder()
                        .seriesId(series.getSeriesId())
                        .title(series.getTitle())
                        .description(series.getDescription())
                        .thumbnailUrl(series.getThumbnailUrl())
                        .mentorId(mentorId)
                        .createdAt(series.getCreatedAt())
                        .build();

                dto.setMentorNickname(nickname);

                content.add(dto);

            } catch (Exception e) {
                log.warn("Failed to fetch mentor nickname for mentorId={}", series.getMentorId(), e);
            }
        }

        return new PageImpl<>(content, pageable, allSeries.getTotalElements());
    }

    @Transactional(readOnly = true)
    public Page<GetMySeriesListResponseDto> getMentorSeriesList(Long mentorId, int page, int size) {

        try {
            mentorFeignClient.getUserIdByMentorId(mentorId); // 404 발생 시 예외 터짐
        } catch (FeignException.NotFound e) {
            throw new BusinessException(ErrorCode.MENTOR_NOT_FOUND);
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<Series> seriesPage = seriesRepository.findAllByMentorIdOrderByCreatedAtDesc(mentorId, pageable);
        return seriesPage.map(seriesMapper::toMySeriesListDto);
    }


    @Transactional(readOnly = true)
    public Page<GetSeriesColumnsResponseDto> getSeriesColumns(Long seriesId, int page, int size) {
        /* 시리즈 존재 여부 확인 */
        Series series = seriesRepository.findById(seriesId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SERIES_NOT_FOUND));

        Pageable pageable = PageRequest.of(page, size);

        /* 시리즈에 속한 칼럼 페이징 조회 */
        Page<Column> columnsPage = columnRepository.findAllBySeries_SeriesId(seriesId, pageable);

        /* DTO로 변환 후 반환 */
        return columnsPage
                .map(seriesMapper::toSeriesColumnDto);
    }

    @Transactional(readOnly = true)
    public Page<GetMySeriesListResponseDto> getMySeriesList(Long userId, int page, int size) {
        Long mentorId = mentorFeignClient.getMentorIdByUserId(userId).getData();
        Pageable pageable = PageRequest.of(page, size);

        Page<Series> seriesPage = seriesRepository.findAllByMentorIdOrderByCreatedAtDesc(mentorId, pageable);
        return seriesPage.map(seriesMapper::toMySeriesListDto);
    }

    // 시리즈 검색 기능
    @Transactional(readOnly = true)
    public Page<GetSeriesListResponseDto> searchPublicSeriesList(SearchCondition condition, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Series> seriesPage = seriesRepository.searchSeriesByKeyword(condition.getKeyword(), pageable);

        List<GetSeriesListResponseDto> content = new ArrayList<>();

        for (Series series : seriesPage.getContent()) {
            try {
                Long mentorId = series.getMentorId();
                var mentorResponse = mentorFeignClient.getUserIdByMentorId(mentorId);
                if (mentorResponse.getData() == null) continue;

                Long userId = mentorResponse.getData();
                var nickname = userFeignClient.getNicknameByUserId(userId).getData();
                if (nickname == null) continue;

                GetSeriesListResponseDto dto = GetSeriesListResponseDto.builder()
                        .seriesId(series.getSeriesId())
                        .title(series.getTitle())
                        .description(series.getDescription())
                        .thumbnailUrl(series.getThumbnailUrl())
                        .mentorId(series.getMentorId())
                        .createdAt(series.getCreatedAt())
                        .build();

                dto.setMentorNickname(nickname);
                content.add(dto);

            } catch (Exception e) {
                log.warn("멘토 닉네임 조회 실패: mentorId={}, seriesId={}, title={}",
                        series.getMentorId(), series.getSeriesId(), series.getTitle(), e);
            }
        }

        return new PageImpl<>(content, pageable, seriesPage.getTotalElements());
    }


    @Transactional(readOnly = true)
    public int getColumnCountBySeriesId(Long seriesId) {
        return columnRepository.findAllBySeries_SeriesId(seriesId).size();
    }
}

