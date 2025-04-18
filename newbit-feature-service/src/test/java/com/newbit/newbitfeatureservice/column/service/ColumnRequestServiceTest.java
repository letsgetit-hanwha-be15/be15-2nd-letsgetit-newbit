package com.newbit.newbitfeatureservice.column.service;

import com.newbit.newbitfeatureservice.client.user.MentorFeignClient;
import com.newbit.newbitfeatureservice.client.user.UserFeignClient;
import com.newbit.newbitfeatureservice.column.domain.Column;
import com.newbit.newbitfeatureservice.column.domain.ColumnRequest;
import com.newbit.newbitfeatureservice.column.domain.Series;
import com.newbit.newbitfeatureservice.column.dto.request.CreateColumnRequestDto;
import com.newbit.newbitfeatureservice.column.dto.request.DeleteColumnRequestDto;
import com.newbit.newbitfeatureservice.column.dto.request.UpdateColumnRequestDto;
import com.newbit.newbitfeatureservice.column.dto.response.CreateColumnResponseDto;
import com.newbit.newbitfeatureservice.column.dto.response.DeleteColumnResponseDto;
import com.newbit.newbitfeatureservice.column.dto.response.GetMyColumnRequestResponseDto;
import com.newbit.newbitfeatureservice.column.dto.response.UpdateColumnResponseDto;
import com.newbit.newbitfeatureservice.column.enums.RequestType;
import com.newbit.newbitfeatureservice.column.mapper.ColumnMapper;
import com.newbit.newbitfeatureservice.column.repository.ColumnRepository;
import com.newbit.newbitfeatureservice.column.repository.ColumnRequestRepository;
import com.newbit.newbitfeatureservice.column.repository.SeriesRepository;
import com.newbit.newbitfeatureservice.common.dto.ApiResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ColumnRequestServiceTest {

    @Mock
    private ColumnRepository columnRepository;

    @Mock
    private ColumnRequestRepository columnRequestRepository;

    @Mock
    private SeriesRepository seriesRepository;

    @Mock
    private ColumnMapper columnMapper;

    @Mock
    private MentorFeignClient mentorFeignClient;

    @InjectMocks
    private ColumnRequestService columnRequestService;

    @Test
    @DisplayName("칼럼 등록 요청 - 성공")
    void createColumnRequest_success() {
        // given
        Long userId = 10L;
        Long mentorId = 1L;

        CreateColumnRequestDto dto = CreateColumnRequestDto.builder()
                .title("테스트 제목")
                .content("테스트 내용")
                .price(1000)
                .seriesId(1L)
                .thumbnailUrl("https://test.com/thumb.jpg")
                .build();

        Column column = Column.builder().columnId(1L).mentorId(mentorId).build();
        Series series = Series.builder().seriesId(1L).build();
        ColumnRequest columnRequest = ColumnRequest.builder().columnRequestId(100L).build();

        when(seriesRepository.findById(1L)).thenReturn(Optional.of(series));
        when(mentorFeignClient.getMentorIdByUserId(userId)).thenReturn(ApiResponse.success(mentorId));
        when(columnMapper.toColumn(dto, mentorId, series)).thenReturn(column);
        when(columnRepository.save(column)).thenReturn(column);
        when(columnMapper.toColumnRequest(dto, column)).thenReturn(columnRequest);
        when(columnRequestRepository.save(columnRequest)).thenReturn(columnRequest);

        // when
        CreateColumnResponseDto response = columnRequestService.createColumnRequest(dto, userId);

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
                .build();

        Column column = Column.builder()
                .columnId(columnId)
                .build();

        ColumnRequest columnRequest = ColumnRequest.builder()
                .columnRequestId(200L)
                .requestType(RequestType.DELETE)
                .isApproved(false)
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

    @DisplayName("멘토 본인 칼럼 요청 조회 - 성공")
    @Test
    void getMyColumnRequests_success() {
        // given
        Long userId = 10L;
        Long mentorId = 1L;

        Column column = Column.builder()
                .columnId(100L)
                .mentorId(mentorId)
                .build();

        ColumnRequest columnRequest = ColumnRequest.builder()
                .columnRequestId(1L)
                .requestType(RequestType.CREATE)
                .isApproved(false)
                .updatedTitle("요청 제목")
                .updatedPrice(1000)
                .column(column)
                .build();

        List<ColumnRequest> columnRequests = List.of(columnRequest);

        GetMyColumnRequestResponseDto dto = GetMyColumnRequestResponseDto.builder()
                .columnRequestId(1L)
                .requestType(RequestType.CREATE)
                .isApproved(false)
                .title("요청 제목")
                .price(1000)
                .createdAt(columnRequest.getCreatedAt())
                .build();

        when(mentorFeignClient.getMentorIdByUserId(userId)).thenReturn(ApiResponse.success(mentorId));
        when(columnRequestRepository.findAllByColumn_MentorIdOrderByCreatedAtDesc(mentorId))
                .thenReturn(columnRequests);
        when(columnMapper.toMyColumnRequestResponseDto(columnRequest)).thenReturn(dto);

        // when
        List<GetMyColumnRequestResponseDto> result = columnRequestService.getMyColumnRequests(userId);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getColumnRequestId()).isEqualTo(1L);
        assertThat(result.get(0).getRequestType()).isEqualTo(RequestType.CREATE);
        assertThat(result.get(0).getTitle()).isEqualTo("요청 제목");

        verify(mentorFeignClient).getMentorIdByUserId(userId);
        verify(columnRequestRepository).findAllByColumn_MentorIdOrderByCreatedAtDesc(mentorId);
        verify(columnMapper).toMyColumnRequestResponseDto(columnRequest);
    }
}
