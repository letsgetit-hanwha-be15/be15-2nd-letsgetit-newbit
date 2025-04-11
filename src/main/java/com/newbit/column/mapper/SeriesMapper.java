package com.newbit.column.mapper;

import com.newbit.column.dto.request.CreateSeriesRequestDto;
import com.newbit.column.dto.response.CreateSeriesResponseDto;
import com.newbit.column.domain.Series;
import com.newbit.column.dto.response.UpdateSeriesResponseDto;
import org.springframework.stereotype.Component;

@Component
public class SeriesMapper {

    public Series toSeries(CreateSeriesRequestDto dto) {
        return Series.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .thumbnailUrl(dto.getThumbnailUrl())
                .build();
    }

    public CreateSeriesResponseDto toCreateSeriesResponseDto(Series series) {
        return CreateSeriesResponseDto.builder()
                .seriesId(series.getSeriesId())
                .build();
    }

    public UpdateSeriesResponseDto toUpdateSeriesResponseDto(Series series) {
        return UpdateSeriesResponseDto.builder()
                .seriesId(series.getSeriesId())
                .title(series.getTitle())
                .description(series.getDescription())
                .thumbnailUrl(series.getThumbnailUrl())
                .build();
    }
}

