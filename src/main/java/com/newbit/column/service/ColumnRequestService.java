package com.newbit.column.service;

import com.newbit.column.dto.request.CreateColumnRequestDto;
import com.newbit.column.dto.request.DeleteColumnRequestDto;
import com.newbit.column.dto.request.UpdateColumnRequestDto;
import com.newbit.column.dto.response.CreateColumnResponseDto;
import com.newbit.column.dto.response.DeleteColumnResponseDto;
import com.newbit.column.dto.response.UpdateColumnResponseDto;
import com.newbit.column.domain.Column;
import com.newbit.column.domain.ColumnRequest;
import com.newbit.column.enums.RequestType;
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
        ColumnRequest request = columnMapper.toColumnRequest(dto, savedColumn);

        ColumnRequest saved = columnRequestRepository.save(request);

        return CreateColumnResponseDto.builder()
                .columnRequestId(saved.getColumnRequestId())
                .build();
    }

    public UpdateColumnResponseDto updateColumnRequest(UpdateColumnRequestDto dto, Long columnId) {
        // 1. columnId로 Column 조회
        Column column = columnRepository.findById(columnId)
                .orElseThrow(() -> new IllegalArgumentException("해당 칼럼을 찾을 수 없습니다. columnId = " + columnId));

        // 2. ColumnRequest 생성
        ColumnRequest request = ColumnRequest.builder()
                .requestType(RequestType.UPDATE)
                .isApproved(false)
                .updatedTitle(dto.getTitle())
                .updatedContent(dto.getContent())
                .updatedPrice(dto.getPrice())
                .updatedThumbnailUrl(dto.getThumbnailUrl())
                .column(column)
                .build();

        // 3. 저장
        ColumnRequest saved = columnRequestRepository.save(request);

        // 4. 응답
        return UpdateColumnResponseDto.builder()
                .columnRequestId(saved.getColumnRequestId())
                .build();
    }

    public DeleteColumnResponseDto deleteColumnRequest(DeleteColumnRequestDto dto, Long columnId) {
        Column column = columnRepository.findById(columnId)
                .orElseThrow(() -> new IllegalArgumentException("해당 칼럼을 찾을 수 없습니다. columnId = " + columnId));

        ColumnRequest request = ColumnRequest.builder()
                .requestType(RequestType.DELETE)
                .isApproved(false)
                .column(column)
                .build();

        ColumnRequest saved = columnRequestRepository.save(request);

        return DeleteColumnResponseDto.builder()
                .columnRequestId(saved.getColumnRequestId())
                .build();
    }
}
