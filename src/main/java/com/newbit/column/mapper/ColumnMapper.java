package com.newbit.column.mapper;

import com.newbit.column.dto.request.CreateColumnRequestDto;
import com.newbit.column.entity.ColumnRequest;
import org.springframework.stereotype.Component;

@Component
public class ColumnMapper {

    public ColumnRequest toEntity(CreateColumnRequestDto dto, Long mentorId) {
        return ColumnRequest.createdColumnRequest(dto, mentorId);
    }
}
