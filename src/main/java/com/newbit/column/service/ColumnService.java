package com.newbit.column.service;

import com.newbit.column.domain.Column;
import com.newbit.column.dto.response.GetColumnDetailResponseDto;
import com.newbit.column.repository.ColumnRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ColumnService {

    private final ColumnRepository columnRepository;

    public GetColumnDetailResponseDto getColumnDetail(Long columnId) {
        Column column = columnRepository.findById(columnId)
                .orElseThrow(() -> new IllegalArgumentException("해당 칼럼을 찾을 수 없습니다. columnId = " + columnId));

        if(!column.isPublic()) {
            throw new IllegalArgumentException("공개된 칼럼이 아닙니다.");
        }

        return GetColumnDetailResponseDto.builder()
                .columnId(column.getColumnId())
                .title(column.getTitle())
                .content(column.getContent())
                .price(column.getPrice())
                .thumbnailUrl(column.getThumbnailUrl())
                .likeCount(column.getLikeCount())
                .mentorId(column.getMentorId())
                .build();
    }
}
