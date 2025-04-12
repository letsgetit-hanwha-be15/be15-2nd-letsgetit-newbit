package com.newbit.column.service;

import com.newbit.column.domain.Column;
import com.newbit.column.dto.request.UpdateSeriesRequestDto;
import com.newbit.column.dto.response.*;
import com.newbit.column.repository.ColumnRepository;
import com.newbit.column.dto.request.CreateSeriesRequestDto;
import com.newbit.column.domain.Series;
import com.newbit.column.mapper.SeriesMapper;
import com.newbit.column.repository.SeriesRepository;
import com.newbit.user.entity.Mentor;
import com.newbit.user.service.MentorService;
import com.newbit.common.exception.BusinessException;
import com.newbit.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeriesService {

    private final SeriesRepository seriesRepository;
    private final ColumnRepository columnRepository;
    private final MentorService mentorService;
    private final SeriesMapper seriesMapper;

    @Transactional
    public CreateSeriesResponseDto createSeries(CreateSeriesRequestDto dto, Long userId) {
        // 1. 유저 → 멘토 엔티티 조회
        Mentor mentor = mentorService.getMentorEntityByUserId(userId);

        // 2. 빈 칼럼 리스트 방지
        if (dto.getColumnIds() == null || dto.getColumnIds().isEmpty()) {
            throw new BusinessException(ErrorCode.SERIES_CREATION_REQUIRES_COLUMNS);
        }

        // 3. 칼럼 ID에 해당하는 Column 리스트 조회
        List<Column> columns = columnRepository.findAllById(dto.getColumnIds());

        if (columns.size() != dto.getColumnIds().size()) {
            throw new BusinessException(ErrorCode.COLUMN_NOT_FOUND);
        }

        // 4. 본인 칼럼인지 확인
        boolean hasInvalidOwner = columns.stream()
                .anyMatch(column -> !column.getMentor().getMentorId().equals(mentor.getMentorId()));
        if (hasInvalidOwner) {
            throw new BusinessException(ErrorCode.COLUMN_NOT_OWNED);
        }

        // 5. 이미 시리즈에 포함된 칼럼인지 확인
        boolean hasAlreadyGrouped = columns.stream().anyMatch(column -> column.getSeries() != null);
        if (hasAlreadyGrouped) {
            throw new BusinessException(ErrorCode.COLUMN_ALREADY_IN_SERIES);
        }

        // 6. 시리즈 저장
        Series series = seriesRepository.save(seriesMapper.toSeries(dto));

        // 7. 각 칼럼에 시리즈 연결
        columns.forEach(column -> column.updateSeries(series));
        columnRepository.saveAll(columns);

        // 8. 응답 반환
        return seriesMapper.toCreateSeriesResponseDto(series);
    }

    @Transactional
    public UpdateSeriesResponseDto updateSeries(Long seriesId, UpdateSeriesRequestDto dto, Long userId) {
        // 1. 멘토 조회
        Mentor mentor = mentorService.getMentorEntityByUserId(userId);

        // 2. 시리즈 조회
        Series series = seriesRepository.findById(seriesId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SERIES_NOT_FOUND));

        // 칼럼 ID 검증
        List<Column> columns = columnRepository.findAllById(dto.getColumnIds());

        if (columns.size() != dto.getColumnIds().size()) {
            throw new BusinessException(ErrorCode.COLUMN_NOT_FOUND);
        }

        boolean hasInvalidOwner = columns.stream()
                .anyMatch(column -> !column.getMentor().getMentorId().equals(mentor.getMentorId()));

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
        Mentor mentor = mentorService.getMentorEntityByUserId(userId);

        Series series = seriesRepository.findById(seriesId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SERIES_NOT_FOUND));

        // 본인의 시리즈인지 확인
        List<Column> columns = columnRepository.findAllBySeries_SeriesId(seriesId);

        boolean isOwner = columns.stream().allMatch(
                column -> column.getMentor().getMentorId().equals(mentor.getMentorId())
        );
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
    public GetSeriesDetailResponseDto getSeriesDetail(Long seriesId) {
        Series series = seriesRepository.findById(seriesId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SERIES_NOT_FOUND));

        return seriesMapper.toGetSeriesDetailResponseDto(series);
    }

    @Transactional(readOnly = true)
    public List<GetMySeriesListResponseDto> getMySeriesList(Long userId) {
        Mentor mentor = mentorService.getMentorEntityByUserId(userId);
        List<Series> seriesList = seriesRepository.findAllByMentor_MentorIdOrderByCreatedAtDesc(mentor.getMentorId());
        return seriesList.stream()
                .map(seriesMapper::toMySeriesListDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<GetSeriesColumnsResponseDto> getSeriesColumns(Long seriesId) {
        /* 시리즈 존재 여부 확인 */
        Series series = seriesRepository.findById(seriesId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SERIES_NOT_FOUND));

        /* 시리즈에 속한 칼럼 조회 */
        List<Column> columns = columnRepository.findAllBySeries_SeriesId(seriesId);

        /* DTO로 변환 후 반환 */
        return columns.stream()
                .map(seriesMapper::toSeriesColumnDto)
                .toList();
    }
}

