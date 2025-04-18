package com.newbit.newbitfeatureservice.column.service;

import com.newbit.newbitfeatureservice.column.domain.Column;
import com.newbit.newbitfeatureservice.column.domain.ColumnRequest;
import com.newbit.newbitfeatureservice.column.domain.Series;
import com.newbit.newbitfeatureservice.column.dto.request.ApproveColumnRequestDto;
import com.newbit.newbitfeatureservice.column.dto.request.RejectColumnRequestDto;
import com.newbit.newbitfeatureservice.column.dto.response.AdminColumnResponseDto;
import com.newbit.newbitfeatureservice.column.enums.RequestType;
import com.newbit.newbitfeatureservice.column.mapper.AdminColumnMapper;
import com.newbit.newbitfeatureservice.column.repository.ColumnRequestRepository;
import com.newbit.newbitfeatureservice.common.exception.BusinessException;
import com.newbit.newbitfeatureservice.common.exception.ErrorCode;
import com.newbit.newbitfeatureservice.notification.command.application.service.NotificationCommandService;
import com.newbit.newbitfeatureservice.subscription.service.SubscriptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminColumnServiceTest {

    @InjectMocks
    private AdminColumnService adminColumnService;

    @Mock
    private ColumnRequestRepository columnRequestRepository;

    @Mock
    private AdminColumnMapper adminColumnMapper;

    @Mock
    private NotificationCommandService notificationCommandService;

    @Mock
    private SubscriptionService subscriptionService;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("칼럼 등록 요청 승인 - 성공")
    void approveCreateColumnRequest_success() {
        // given
        Long requestId = 1L;
        Long adminUserId = 100L;
        Long mentorId = 10L;
        Long seriesId = 5L;

        ApproveColumnRequestDto dto = new ApproveColumnRequestDto();
        ReflectionTestUtils.setField(dto, "columnRequestId", requestId);

        Series series = Series.builder().seriesId(seriesId).title("테스트 시리즈").build();
        Column column = Column.builder().columnId(10L).title("테스트 칼럼").series(series).mentorId(mentorId).build();
        ColumnRequest request = ColumnRequest.builder()
                .columnRequestId(requestId)
                .requestType(RequestType.CREATE)
                .isApproved(null)
                .column(column)
                .build();

        AdminColumnResponseDto responseDto = AdminColumnResponseDto.builder()
                .requestId(requestId)
                .requestType("CREATE")
                .isApproved(true)
                .columnId(column.getColumnId())
                .build();

        when(columnRequestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(adminColumnMapper.toDto(any())).thenReturn(responseDto);

        // when
        AdminColumnResponseDto result = adminColumnService.approveCreateColumnRequest(dto, adminUserId);

        // then
        assertThat(result.getRequestId()).isEqualTo(requestId);
        assertThat(result.getIsApproved()).isTrue();
        verify(columnRequestRepository).findById(requestId);
    }

    @Test
    @DisplayName("칼럼 등록 요청 승인 - 잘못된 요청 타입 예외")
    void approveCreateColumnRequest_invalidRequestType_throwsException() {
        // given
        Long requestId = 2L;
        ApproveColumnRequestDto dto = new ApproveColumnRequestDto();
        ReflectionTestUtils.setField(dto, "columnRequestId", requestId);

        ColumnRequest request = ColumnRequest.builder()
                .columnRequestId(requestId)
                .requestType(RequestType.UPDATE) // 잘못된 타입
                .build();

        when(columnRequestRepository.findById(requestId)).thenReturn(Optional.of(request));

        // when & then
        assertThatThrownBy(() -> adminColumnService.approveCreateColumnRequest(dto, 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.INVALID_REQUEST_TYPE.getMessage());
    }

    @Test
    @DisplayName("칼럼 등록 요청 거절 - 성공")
    void rejectCreateColumnRequest_success() {
        // given
        Long requestId = 3L;
        Long adminUserId = 101L;
        Long mentorId = 10L;
        Long seriesId = 5L;

        RejectColumnRequestDto dto = new RejectColumnRequestDto();
        ReflectionTestUtils.setField(dto, "columnRequestId", requestId);
        ReflectionTestUtils.setField(dto, "reason", "부적절한 내용");

        Series series = Series.builder().seriesId(seriesId).title("테스트 시리즈").build();
        Column column = Column.builder().columnId(10L).title("테스트 칼럼").mentorId(mentorId).build();
        ColumnRequest request = ColumnRequest.builder()
                .columnRequestId(requestId)
                .requestType(RequestType.CREATE)
                .isApproved(null)
                .column(column)
                .build();

        AdminColumnResponseDto responseDto = AdminColumnResponseDto.builder()
                .requestId(requestId)
                .requestType("CREATE")
                .isApproved(false)
                .columnId(column.getColumnId())
                .build();

        when(columnRequestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(adminColumnMapper.toDto(any())).thenReturn(responseDto);

        // when
        AdminColumnResponseDto result = adminColumnService.rejectCreateColumnRequest(dto, adminUserId);

        // then
        assertThat(result.getIsApproved()).isFalse();
        assertThat(result.getColumnId()).isEqualTo(column.getColumnId());
    }

    @Test
    @DisplayName("칼럼 등록 요청 거절 - 요청 없음 예외")
    void rejectCreateColumnRequest_requestNotFound_throwsException() {
        // given
        Long requestId = 99L;
        RejectColumnRequestDto dto = new RejectColumnRequestDto();
        ReflectionTestUtils.setField(dto, "columnRequestId", requestId);

        when(columnRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminColumnService.rejectCreateColumnRequest(dto, 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.COLUMN_REQUEST_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("칼럼 수정 요청 승인 - 성공")
    void approveUpdateColumnRequest_success() {
        // given
        Long columnRequestId = 1L;


        Column column = Column.builder()
                .columnId(200L)
                .mentorId(20L)
                .title("기존 제목")
                .build();

        ColumnRequest request = ColumnRequest.builder()
                .columnRequestId(columnRequestId)
                .requestType(RequestType.UPDATE)
                .column(column)
                .updatedTitle("수정 제목")
                .updatedContent("수정 내용")
                .updatedPrice(5000)
                .updatedThumbnailUrl("https://example.com/thumb.jpg")
                .build();

        AdminColumnResponseDto expectedDto = AdminColumnResponseDto.builder()
                .requestId(columnRequestId)
                .requestType("UPDATE")
                .isApproved(true)
                .columnId(column.getColumnId())
                .build();

        when(columnRequestRepository.findById(columnRequestId)).thenReturn(Optional.of(request));
        when(adminColumnMapper.toDto(request)).thenReturn(expectedDto);

        // when
        AdminColumnResponseDto result = adminColumnService.approveUpdateColumnRequest(
                createApproveDto(columnRequestId), 1L);

        // then
        assertThat(result.getRequestId()).isEqualTo(columnRequestId);
        assertThat(result.getIsApproved()).isTrue();
        verify(columnRequestRepository).findById(columnRequestId);
    }

    @Test
    @DisplayName("칼럼 수정 요청 승인 - 요청 타입이 UPDATE가 아닌 경우 예외 발생")
    void approveUpdateColumnRequest_invalidType() {
        // given
        Long columnRequestId = 2L;
        ColumnRequest request = ColumnRequest.builder()
                .columnRequestId(columnRequestId)
                .requestType(RequestType.CREATE)
                .build();

        when(columnRequestRepository.findById(columnRequestId)).thenReturn(Optional.of(request));

        // when & then
        assertThatThrownBy(() -> adminColumnService.approveUpdateColumnRequest(
                createApproveDto(columnRequestId), 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.INVALID_REQUEST_TYPE.getMessage());
    }

    @Test
    @DisplayName("칼럼 수정 요청 거절 - 성공")
    void rejectUpdateColumnRequest_success() {
        // given
        Long columnRequestId = 3L;
        Long adminUserId = 99L;
        Long mentorId = 10L;

        Column column = Column.builder().columnId(10L).title("테스트 칼럼").mentorId(mentorId).build();
        ColumnRequest request = ColumnRequest.builder()
                .columnRequestId(columnRequestId)
                .requestType(RequestType.UPDATE)
                .column(column)
                .build();

        AdminColumnResponseDto expectedDto = AdminColumnResponseDto.builder()
                .requestId(columnRequestId)
                .requestType("UPDATE")
                .isApproved(false)
                .columnId(5L)
                .build();

        when(columnRequestRepository.findById(columnRequestId)).thenReturn(Optional.of(request));
        when(adminColumnMapper.toDto(request)).thenReturn(expectedDto);

        // when
        AdminColumnResponseDto result = adminColumnService.rejectUpdateColumnRequest(
                createRejectDto(columnRequestId, "사유입니다."), adminUserId);

        // then
        assertThat(result.getRequestId()).isEqualTo(columnRequestId);
        verify(columnRequestRepository).findById(columnRequestId);
    }

    @Test
    @DisplayName("칼럼 수정 요청 거절 - 요청이 존재하지 않는 경우 예외 발생")
    void rejectUpdateColumnRequest_notFound() {
        // given
        Long columnRequestId = 4L;
        when(columnRequestRepository.findById(columnRequestId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminColumnService.rejectUpdateColumnRequest(
                createRejectDto(columnRequestId, "없음"), 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.COLUMN_REQUEST_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("칼럼 삭제 요청 승인 - 성공")
    void approveDeleteColumnRequest_success() {
        // given
        Long columnRequestId = 11L;
        Long adminUserId = 99L;
        Long mentorId = 10L;

        Column column = Column.builder()
                .columnId(100L)
                .title("삭제 대상 칼럼")
                .mentorId(mentorId)
                .build();

        ColumnRequest request = ColumnRequest.builder()
                .columnRequestId(columnRequestId)
                .requestType(RequestType.DELETE)
                .column(column)
                .build();

        AdminColumnResponseDto expectedDto = AdminColumnResponseDto.builder()
                .requestId(columnRequestId)
                .requestType("DELETE")
                .isApproved(true)
                .columnId(column.getColumnId())
                .build();

        when(columnRequestRepository.findById(columnRequestId)).thenReturn(Optional.of(request));
        when(adminColumnMapper.toDto(request)).thenReturn(expectedDto);

        // when
        AdminColumnResponseDto result = adminColumnService.approveDeleteColumnRequest(
                createApproveDto(columnRequestId), adminUserId);

        // then
        assertThat(result.getRequestId()).isEqualTo(columnRequestId);
        assertThat(result.getIsApproved()).isTrue();
        verify(columnRequestRepository).findById(columnRequestId);
    }
    @Test
    @DisplayName("칼럼 삭제 요청 승인 - 요청 타입이 DELETE가 아닌 경우 예외 발생")
    void approveDeleteColumnRequest_invalidType() {
        // given
        Long columnRequestId = 12L;
        ColumnRequest request = ColumnRequest.builder()
                .columnRequestId(columnRequestId)
                .requestType(RequestType.UPDATE)
                .build();

        when(columnRequestRepository.findById(columnRequestId)).thenReturn(Optional.of(request));

        // when & then
        assertThatThrownBy(() -> adminColumnService.approveDeleteColumnRequest(
                createApproveDto(columnRequestId), 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.INVALID_REQUEST_TYPE.getMessage());
    }

    @Test
    @DisplayName("칼럼 삭제 요청 거절 - 성공")
    void rejectDeleteColumnRequest_success() {
        // given
        Long columnRequestId = 13L;
        Long adminUserId = 99L;
        Long mentorId = 10L;
        Column column = Column.builder().columnId(10L).title("테스트 칼럼").mentorId(mentorId).build();
        ColumnRequest request = ColumnRequest.builder()
                .columnRequestId(columnRequestId)
                .requestType(RequestType.DELETE)
                .column(column)
                .build();

        AdminColumnResponseDto expectedDto = AdminColumnResponseDto.builder()
                .requestId(columnRequestId)
                .requestType("DELETE")
                .isApproved(false)
                .columnId(3L)
                .build();

        when(columnRequestRepository.findById(columnRequestId)).thenReturn(Optional.of(request));
        when(adminColumnMapper.toDto(request)).thenReturn(expectedDto);

        // when
        AdminColumnResponseDto result = adminColumnService.rejectDeleteColumnRequest(
                createRejectDto(columnRequestId, "삭제 거절"), adminUserId);

        // then
        assertThat(result.getRequestId()).isEqualTo(columnRequestId);
    }

    @Test
    @DisplayName("칼럼 삭제 요청 거절 - 요청이 존재하지 않는 경우 예외 발생")
    void rejectDeleteColumnRequest_notFound() {
        // given
        Long columnRequestId = 14L;
        when(columnRequestRepository.findById(columnRequestId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminColumnService.rejectDeleteColumnRequest(
                createRejectDto(columnRequestId, "없는 요청"), 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.COLUMN_REQUEST_NOT_FOUND.getMessage());
    }

    // ===== DTO 헬퍼 메서드 =====

    private ApproveColumnRequestDto createApproveDto(Long id) {
        ApproveColumnRequestDto dto = new ApproveColumnRequestDto();
        setField(dto, "columnRequestId", id);
        return dto;
    }

    private RejectColumnRequestDto createRejectDto(Long id, String reason) {
        RejectColumnRequestDto dto = new RejectColumnRequestDto();
        setField(dto, "columnRequestId", id);
        setField(dto, "reason", reason);
        return dto;
    }

    // 필드 값 세팅용 리플렉션 헬퍼
    private void setField(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}