package com.newbit.newbitfeatureservice.column.mapper;

import com.newbit.newbitfeatureservice.column.domain.Series;
import com.newbit.newbitfeatureservice.column.dto.request.CreateColumnRequestDto;
import com.newbit.newbitfeatureservice.column.domain.Column;
import com.newbit.newbitfeatureservice.column.domain.ColumnRequest;
import com.newbit.newbitfeatureservice.column.dto.response.AdminColumnRequestResponseDto;
import com.newbit.newbitfeatureservice.column.dto.response.GetMyColumnListResponseDto;
import com.newbit.newbitfeatureservice.column.dto.response.GetMyColumnRequestResponseDto;
import com.newbit.newbitfeatureservice.column.enums.RequestType;
import org.springframework.stereotype.Component;

@Component
public class ColumnMapper {

    public Column toColumn(CreateColumnRequestDto dto, Long mentorId, Series series) {
        return Column.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .price(dto.getPrice())
                .series(series)
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

    public GetMyColumnRequestResponseDto toMyColumnRequestResponseDto(ColumnRequest columnRequest) {
        Column column = columnRequest.getColumn();

        boolean isCreate = columnRequest.getRequestType() == RequestType.CREATE;

        return GetMyColumnRequestResponseDto.builder()
                .columnRequestId(columnRequest.getColumnRequestId())
                .requestType(columnRequest.getRequestType())
                .isApproved(columnRequest.getIsApproved())

                // CREATE 요청이면 Column 테이블의 값 사용, 아니면 updated 값 사용
                .title(isCreate ? column.getTitle() : columnRequest.getUpdatedTitle())
                .price(isCreate
                        ? column.getPrice()
                        : columnRequest.getUpdatedPrice() != null
                        ? columnRequest.getUpdatedPrice()
                        : 0)
                .thumbnailUrl(isCreate ? column.getThumbnailUrl() : columnRequest.getUpdatedThumbnailUrl())

                .createdAt(columnRequest.getCreatedAt())
                .rejectedReason(columnRequest.getRejectedReason())
                .build();
    }

    public GetMyColumnListResponseDto toMyColumnListDto(Column column) {
        return GetMyColumnListResponseDto.builder()
                .columnId(column.getColumnId())
                .title(column.getTitle())
                .thumbnailUrl(column.getThumbnailUrl())
                .price(column.getPrice())
                .createdAt(column.getCreatedAt())
                .build();
    }

    public AdminColumnRequestResponseDto toAdminColumnRequestResponseDto(ColumnRequest request, String mentorNickname) {
        Column column = request.getColumn();
        boolean isCreate = request.getRequestType() == RequestType.CREATE;

        return AdminColumnRequestResponseDto.builder()
                .columnRequestId(request.getColumnRequestId())
                .requestType(request.getRequestType())
                .isApproved(request.getIsApproved())
                .title(isCreate ? column.getTitle() : request.getUpdatedTitle())
                .price(isCreate
                        ? column.getPrice()
                        : request.getUpdatedPrice() != null
                        ? request.getUpdatedPrice()
                        : 0)
                .thumbnailUrl(isCreate ? column.getThumbnailUrl() : request.getUpdatedThumbnailUrl())
                .createdAt(request.getCreatedAt())
                .rejectedReason(request.getRejectedReason())
                .mentorNickname(mentorNickname)
                .content(isCreate ? column.getContent() : request.getUpdatedContent())
                .build();
    }

}
