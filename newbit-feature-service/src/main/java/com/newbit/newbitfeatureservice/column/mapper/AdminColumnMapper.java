package com.newbit.newbitfeatureservice.column.mapper;

import com.newbit.newbitfeatureservice.column.domain.ColumnRequest;
import com.newbit.newbitfeatureservice.column.dto.response.AdminColumnResponseDto;
import org.springframework.stereotype.Component;

@Component
public class AdminColumnMapper {

    public AdminColumnResponseDto toDto(ColumnRequest request) {
        return AdminColumnResponseDto.builder()
                .requestId(request.getColumnRequestId())
                .requestType(request.getRequestType().name())
                .isApproved(request.getIsApproved())
                .columnId(request.getColumn().getColumnId())
                .build();
    }
}
