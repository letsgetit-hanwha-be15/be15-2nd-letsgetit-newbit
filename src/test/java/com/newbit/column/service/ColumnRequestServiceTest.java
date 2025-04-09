package com.newbit.column.service;

import com.newbit.column.dto.request.CreateColumnRequestDto;
import com.newbit.column.dto.request.DeleteColumnRequestDto;
import com.newbit.column.dto.request.UpdateColumnRequestDto;
import com.newbit.column.dto.response.CreateColumnResponseDto;
import com.newbit.column.dto.response.DeleteColumnResponseDto;
import com.newbit.column.dto.response.UpdateColumnResponseDto;
import com.newbit.column.domain.Column;
import com.newbit.column.enums.RequestType;
import com.newbit.column.repository.ColumnRepository;
import com.newbit.column.repository.ColumnRequestRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.newbit.column.domain.ColumnRequest;
import com.newbit.column.mapper.ColumnMapper;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ColumnRequestServiceTest {

    @Mock
    private ColumnRepository columnRepository;

    @Mock
    private ColumnRequestRepository columnRequestRepository;

    @Mock
    private ColumnMapper columnMapper;

    @InjectMocks
    private ColumnRequestService columnRequestService;

    @Test
    @DisplayName("칼럼 등록 요청 - 성공")
    void createColumnRequest_success() {
        // given
        CreateColumnRequestDto dto = CreateColumnRequestDto.builder()
                .title("테스트 제목")
                .content("테스트 내용")
                .price(1000)
                .thumbnailUrl("https://test.com/thumb.jpg")
                .build();

        Column column = Column.builder().columnId(1L).build();
        ColumnRequest columnRequest = ColumnRequest.builder().columnRequestId(100L).build();

        when(columnMapper.toColumn(dto, 1L)).thenReturn(column);
        when(columnRepository.save(column)).thenReturn(column);
        when(columnMapper.toColumnRequest(dto, column)).thenReturn(columnRequest);
        when(columnRequestRepository.save(columnRequest)).thenReturn(columnRequest);

        // when
        CreateColumnResponseDto response = columnRequestService.createColumnRequest(dto, 1L);

        // then
        assertThat(response.getColumnRequestId()).isEqualTo(100L);
        verify(columnRepository).save(column);
        verify(columnRequestRepository).save(columnRequest);
    }

    @Test
    @DisplayName("칼럼 수정 요청 - 성공")
    void updateColumnRequest_success() {
        // given
        Long columnId = 1L;

        UpdateColumnRequestDto dto = UpdateColumnRequestDto.builder()
                .title("수정된 제목")
                .content("수정된 내용")
                .price(3000)
                .thumbnailUrl("https://test.com/thumb.jpg")
                .build();

        Column column = Column.builder().columnId(columnId).build();

        ColumnRequest columnRequest = ColumnRequest.builder()
                .columnRequestId(100L)
                .requestType(RequestType.UPDATE)
                .isApproved(false)
                .updatedTitle(dto.getTitle())
                .updatedContent(dto.getContent())
                .updatedPrice(dto.getPrice())
                .updatedThumbnailUrl(dto.getThumbnailUrl())
                .column(column)
                .build();

        when(columnRepository.findById(columnId)).thenReturn(Optional.of(column));
        when(columnRequestRepository.save(any(ColumnRequest.class))).thenReturn(columnRequest);

        // when
        UpdateColumnResponseDto response = columnRequestService.updateColumnRequest(dto, columnId);

        // then
        assertThat(response.getColumnRequestId()).isEqualTo(100L);
        verify(columnRepository).findById(columnId);
        verify(columnRequestRepository).save(any(ColumnRequest.class));
    }

    @Test
    @DisplayName("칼럼 삭제 요청 - 성공")
    void deleteColumnRequest_success() {
        // given
        Long columnId = 1L;

        DeleteColumnRequestDto dto = DeleteColumnRequestDto.builder()
                .reason("삭제 요청 사유입니다.")
                .build();

        Column column = Column.builder()
                .columnId(columnId)
                .build();

        ColumnRequest columnRequest = ColumnRequest.builder()
                .columnRequestId(200L)
                .requestType(RequestType.DELETE)
                .isApproved(false)
                .rejectedReason(dto.getReason())
                .column(column)
                .build();

        when(columnRepository.findById(columnId)).thenReturn(Optional.of(column));
        when(columnRequestRepository.save(any(ColumnRequest.class))).thenReturn(columnRequest);

        // when
        DeleteColumnResponseDto response = columnRequestService.deleteColumnRequest(dto, columnId);

        // then
        assertThat(response.getColumnRequestId()).isEqualTo(200L);
        verify(columnRepository).findById(columnId);
        verify(columnRequestRepository).save(any(ColumnRequest.class));
    }
}
