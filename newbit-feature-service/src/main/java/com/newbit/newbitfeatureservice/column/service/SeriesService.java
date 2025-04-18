package com.newbit.newbitfeatureservice.column.service;

import java.util.List;

import com.newbit.newbitfeatureservice.client.user.MentorFeignClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newbit.newbitfeatureservice.column.domain.Column;
import com.newbit.newbitfeatureservice.column.domain.Series;
import com.newbit.newbitfeatureservice.column.dto.request.CreateSeriesRequestDto;
import com.newbit.newbitfeatureservice.column.dto.request.UpdateSeriesRequestDto;
import com.newbit.newbitfeatureservice.column.dto.response.CreateSeriesResponseDto;
import com.newbit.newbitfeatureservice.column.dto.response.GetMySeriesListResponseDto;
import com.newbit.newbitfeatureservice.column.dto.response.GetSeriesColumnsResponseDto;
import com.newbit.newbitfeatureservice.column.dto.response.GetSeriesDetailResponseDto;
import com.newbit.newbitfeatureservice.column.dto.response.UpdateSeriesResponseDto;
import com.newbit.newbitfeatureservice.column.mapper.SeriesMapper;
import com.newbit.newbitfeatureservice.column.repository.ColumnRepository;
import com.newbit.newbitfeatureservice.column.repository.SeriesRepository;
import com.newbit.newbitfeatureservice.common.exception.BusinessException;
import com.newbit.newbitfeatureservice.common.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SeriesService {

    private final SeriesRepository seriesRepository;
    private final ColumnRepository columnRepository;
    private final MentorFeignClient mentorFeignClient;
    private final SeriesMapper seriesMapper;

    @Transactional
    public CreateSeriesResponseDto createSeries(CreateSeriesRequestDto dto, Long userId) {
        // 1. 유저 → 멘토 엔티티 조회
        Long mentorId = mentorFeignClient.getMentorIdByUserId(userId).getData();

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
                .anyMatch(column -> !column.getMentorId().equals(mentorId));
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
    public List<GetMySeriesListResponseDto> getMySeriesList(Long userId) {
        Long mentorId = mentorFeignClient.getMentorIdByUserId(userId).getData();
        List<Series> seriesList = seriesRepository.findAllByMentorIdOrderByCreatedAtDesc(mentorId);
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

