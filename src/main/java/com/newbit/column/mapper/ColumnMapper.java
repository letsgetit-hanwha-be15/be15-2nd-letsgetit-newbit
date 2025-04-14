package com.newbit.column.mapper;

import com.newbit.column.domain.Series;
import com.newbit.column.dto.request.CreateColumnRequestDto;
import com.newbit.column.domain.Column;
import com.newbit.column.domain.ColumnRequest;
import com.newbit.column.dto.response.GetMyColumnListResponseDto;
import com.newbit.column.dto.response.GetMyColumnRequestResponseDto;
import com.newbit.column.enums.RequestType;
import com.newbit.user.entity.Mentor;
import org.springframework.stereotype.Component;

@Component
public class ColumnMapper {

    public Column toColumn(CreateColumnRequestDto dto, Mentor mentor, Series series) {
        return Column.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .price(dto.getPrice())
                .series(series)
                .thumbnailUrl(dto.getThumbnailUrl())
                .mentor(mentor)
                .isPublic(false)
                .likeCount(0)
                .build();
    }

    public ColumnRequest toColumnRequest(CreateColumnRequestDto dto, Column column) {
        return ColumnRequest.builder()
                .requestType(RequestType.CREATE)
                .isApproved(false)
                .updatedTitle(dto.getTitle())
                .updatedContent(dto.getContent())
                .updatedPrice(dto.getPrice())
                .updatedThumbnailUrl(dto.getThumbnailUrl())
                .column(column)
                .build();
    }

    public GetMyColumnRequestResponseDto toMyColumnRequestResponseDto(ColumnRequest columnRequest) {
        Column column = columnRequest.getColumn();

        boolean isCreate = columnRequest.getRequestType() == RequestType.CREATE;

        return GetMyColumnRequestResponseDto.builder()
                .columnRequestId(columnRequest.getColumnRequestId())
                .requestType(columnRequest.getRequestType())
                .isApproved(columnRequest.getIsApproved())

                // CREATE 요청이면 Column 테이블의 값 사용, 아니면 updated 값 사용
                .title(isCreate ? column.getTitle() : columnRequest.getUpdatedTitle())
                .price(isCreate ? column.getPrice() : columnRequest.getUpdatedPrice())
                .thumbnailUrl(isCreate ? column.getThumbnailUrl() : columnRequest.getUpdatedThumbnailUrl())

                .createdAt(columnRequest.getCreatedAt())
                .build();
    }

    public GetMyColumnListResponseDto toMyColumnListDto(Column column) {
        return GetMyColumnListResponseDto.builder()
                .columnId(column.getColumnId())
                .title(column.getTitle())
                .thumbnailUrl(column.getThumbnailUrl())
                .price(column.getPrice())
                .likeCount(column.getLikeCount())
                .build();
    }
}
