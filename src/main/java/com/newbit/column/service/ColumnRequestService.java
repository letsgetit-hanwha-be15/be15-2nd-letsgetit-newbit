package com.newbit.column.service;

import com.newbit.column.domain.Series;
import com.newbit.column.dto.request.CreateColumnRequestDto;
import com.newbit.column.dto.request.DeleteColumnRequestDto;
import com.newbit.column.dto.request.UpdateColumnRequestDto;
import com.newbit.column.dto.response.CreateColumnResponseDto;
import com.newbit.column.dto.response.DeleteColumnResponseDto;
import com.newbit.column.dto.response.GetMyColumnRequestResponseDto;
import com.newbit.column.dto.response.UpdateColumnResponseDto;
import com.newbit.column.domain.Column;
import com.newbit.column.domain.ColumnRequest;
import com.newbit.column.enums.RequestType;
import com.newbit.column.mapper.ColumnMapper;
import com.newbit.column.repository.ColumnRepository;
import com.newbit.column.repository.ColumnRequestRepository;
import com.newbit.column.repository.SeriesRepository;
import com.newbit.common.exception.BusinessException;
import com.newbit.common.exception.ErrorCode;
import com.newbit.user.entity.Mentor;
import com.newbit.user.service.MentorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ColumnRequestService {

    private final ColumnRepository columnRepository;
    private final ColumnRequestRepository columnRequestRepository;
    private final SeriesRepository seriesRepository;
    private final MentorService mentorService;
    private final ColumnMapper columnMapper;

    public CreateColumnResponseDto createColumnRequest(CreateColumnRequestDto dto, Long userId) {
        // 1. Mentor 조회
        Mentor mentor = mentorService.getMentorEntityByUserId(userId);

        // 2. 시리즈 조회
        Series series = seriesRepository.findById(dto.getSeriesId())
                .orElseThrow(() -> new BusinessException(ErrorCode.SERIES_NOT_FOUND));

        // 3. Column 저장
        Column column = columnMapper.toColumn(dto, mentor, series);
        Column savedColumn = columnRepository.save(column);

        // 4. ColumnRequest 저장
        ColumnRequest request = columnMapper.toColumnRequest(dto, savedColumn);
        ColumnRequest savedRequest = columnRequestRepository.save(request);

        return CreateColumnResponseDto.builder()
                .columnRequestId(savedRequest.getColumnRequestId())
                .build();
    }

    public UpdateColumnResponseDto updateColumnRequest(UpdateColumnRequestDto dto, Long columnId) {
        // 1. columnId로 Column 조회
        Column column = columnRepository.findById(columnId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COLUMN_NOT_FOUND));

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
                .orElseThrow(() -> new BusinessException(ErrorCode.COLUMN_NOT_FOUND));

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

    public List<GetMyColumnRequestResponseDto> getMyColumnRequests(Long userId) {
        Mentor mentor = mentorService.getMentorEntityByUserId(userId);

        List<ColumnRequest> requests = columnRequestRepository.findAllByColumn_Mentor_MentorIdOrderByCreatedAtDesc(mentor.getMentorId());

        return requests.stream()
                .map(columnMapper::toMyColumnRequestResponseDto)
                .toList();
    }

    public Integer getColumnPriceById(Long columnId) {
        return columnRepository.findById(columnId)
                .map(Column::getPrice)
                .orElseThrow(() -> new BusinessException(ErrorCode.COLUMN_NOT_FOUND));
    }

    public Long getMentorId(Long columnId) {
        return columnRepository.findById(columnId)
                .map(column -> column.getMentor().getMentorId())
                .orElseThrow(() -> new BusinessException(ErrorCode.COLUMN_NOT_FOUND));
    }
}
