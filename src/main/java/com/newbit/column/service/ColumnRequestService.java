package com.newbit.column.service;

import com.newbit.column.dto.request.CreateColumnRequestDto;
import com.newbit.column.dto.response.CreateColumnResponseDto;
import com.newbit.column.entity.Column;
import com.newbit.column.entity.ColumnRequest;
import com.newbit.column.mapper.ColumnMapper;
import com.newbit.column.repository.ColumnRepository;
import com.newbit.column.repository.ColumnRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ColumnRequestService {

    private final ColumnRepository columnRepository;
    private final ColumnRequestRepository columnRequestRepository;
    private final ColumnMapper columnMapper;

    public CreateColumnResponseDto createColumnRequest(CreateColumnRequestDto dto, Long mentorId) {
        // 1. Column 저장
        Column column = columnMapper.toColumn(dto, mentorId);
        Column savedColumn = columnRepository.save(column);

        // 2. ColumnRequest 저장
        Long adminUserId = 1L; // 임시로 지정할 관리자 ID
        ColumnRequest request = columnMapper.toColumnRequest(dto, savedColumn, adminUserId);

        ColumnRequest saved = columnRequestRepository.save(request);

        return CreateColumnResponseDto.builder()
                .columnRequestId(saved.getColumnRequestId())
                .build();
    }
}
