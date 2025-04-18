package com.newbit.newbitfeatureservice.column.mapper;

import com.newbit.newbitfeatureservice.column.domain.Column;
import com.newbit.newbitfeatureservice.column.dto.request.CreateSeriesRequestDto;
import com.newbit.newbitfeatureservice.column.domain.Series;
import com.newbit.newbitfeatureservice.column.dto.response.*;
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

    public GetSeriesDetailResponseDto toGetSeriesDetailResponseDto(Series series) {
        return GetSeriesDetailResponseDto.builder()
                .seriesId(series.getSeriesId())
                .title(series.getTitle())
                .description(series.getDescription())
                .thumbnailUrl(series.getThumbnailUrl())
                .build();
    }

    public GetMySeriesListResponseDto toMySeriesListDto(Series series) {
        return GetMySeriesListResponseDto.builder()
                .seriesId(series.getSeriesId())
                .title(series.getTitle())
                .description(series.getDescription())
                .thumbnailUrl(series.getThumbnailUrl())
                .build();
    }

    public GetSeriesColumnsResponseDto toSeriesColumnDto(Column column) {
        return GetSeriesColumnsResponseDto.builder()
                .columnId(column.getColumnId())
                .title(column.getTitle())
                .thumbnailUrl(column.getThumbnailUrl())
                .price(column.getPrice())
                .likeCount(column.getLikeCount())
                .createdAt(column.getCreatedAt())
                .updatedAt(column.getUpdatedAt())
                .build();
    }
}

