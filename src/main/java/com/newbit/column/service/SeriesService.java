package com.newbit.column.service;

import com.newbit.column.domain.Column;
import com.newbit.column.repository.ColumnRepository;
import com.newbit.column.dto.request.CreateSeriesRequestDto;
import com.newbit.column.dto.response.CreateSeriesResponseDto;
import com.newbit.column.domain.Series;
import com.newbit.column.mapper.SeriesMapper;
import com.newbit.column.repository.SeriesRepository;
import com.newbit.user.entity.Mentor;
import com.newbit.user.service.MentorService;
import com.newbit.common.exception.BusinessException;
import com.newbit.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeriesService {

    private final SeriesRepository seriesRepository;
    private final ColumnRepository columnRepository;
    private final MentorService mentorService;
    private final SeriesMapper seriesMapper;

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
}

