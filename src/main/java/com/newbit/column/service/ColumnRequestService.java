package com.newbit.column.service;

import com.newbit.column.dto.request.CreateColumnRequestDto;
import com.newbit.column.dto.response.CreateColumnResponseDto;
import com.newbit.column.entity.ColumnRequest;
import com.newbit.column.mapper.ColumnMapper;
import com.newbit.column.repository.ColumnRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ColumnRequestService {

    private final ColumnRequestRepository columnRequestRepository;
    private final ColumnMapper columnMapper;

    public CreateColumnResponseDto createColumnRequest(CreateColumnRequestDto dto, Long mentorId) {
        ColumnRequest request = columnMapper.toEntity(dto, mentorId);
        ColumnRequest saved = columnRequestRepository.save(request);

        return CreateColumnResponseDto.builder()
                .columnRequestId(saved.getColumnRequestId())
                .build();
    }
}
