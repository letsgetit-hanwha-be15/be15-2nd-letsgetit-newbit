package com.newbit.column.mapper;

import com.newbit.column.dto.request.CreateColumnRequestDto;
import com.newbit.column.domain.Column;
import com.newbit.column.domain.ColumnRequest;
import com.newbit.column.dto.response.GetMyColumnRequestResponseDto;
import com.newbit.column.enums.RequestType;
import com.newbit.user.entity.Mentor;
import org.springframework.stereotype.Component;

@Component
public class ColumnMapper {

    public Column toColumn(CreateColumnRequestDto dto, Mentor mentor) {
        return Column.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .price(dto.getPrice())
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
        return GetMyColumnRequestResponseDto.builder()
                .columnRequestId(columnRequest.getColumnRequestId())
                .requestType(columnRequest.getRequestType())
                .isApproved(columnRequest.getIsApproved())
                .title(columnRequest.getUpdatedTitle())
                .price(columnRequest.getUpdatedPrice())
                .thumbnailUrl(columnRequest.getUpdatedThumbnailUrl())
                .createdAt(columnRequest.getCreatedAt())
                .build();
    }
}
