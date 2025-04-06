package com.newbit.column.mapper;

import com.newbit.column.dto.request.CreateColumnRequestDto;
import com.newbit.column.entity.Column;
import com.newbit.column.entity.ColumnRequest;
import com.newbit.column.enums.RequestType;
import org.springframework.stereotype.Component;

@Component
public class ColumnMapper {

    public Column toColumn(CreateColumnRequestDto dto, Long mentorId) {
        return Column.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .price(dto.getPrice())
                .thumbnailUrl(dto.getThumbnailUrl())
                .mentorId(mentorId)
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
}
