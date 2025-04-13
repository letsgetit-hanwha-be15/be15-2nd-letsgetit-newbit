package com.newbit.column.service;

import com.newbit.column.domain.Column;
import com.newbit.column.domain.ColumnRequest;
import com.newbit.column.dto.request.ApproveColumnRequestDto;
import com.newbit.column.dto.request.RejectColumnRequestDto;
import com.newbit.column.dto.response.AdminColumnResponseDto;
import com.newbit.column.enums.RequestType;
import com.newbit.column.mapper.AdminColumnMapper;
import com.newbit.column.repository.ColumnRequestRepository;
import com.newbit.common.exception.BusinessException;
import com.newbit.common.exception.ErrorCode;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminColumnServiceTest {

    @InjectMocks
    private AdminColumnService adminColumnService;

    @Mock
    private ColumnRequestRepository columnRequestRepository;

    @Mock
    private AdminColumnMapper adminColumnMapper;

    @BeforeEach
    void setUp() {
        // MockitoAnnotations.openMocks(this); // MockitoExtension으로 대체
    }

    @Test
    @DisplayName("칼럼 등록 요청 승인 - 성공")
    void approveCreateColumnRequest_success() {
        // given
        Long requestId = 1L;
        Long adminUserId = 100L;
        ApproveColumnRequestDto dto = new ApproveColumnRequestDto();
        ReflectionTestUtils.setField(dto, "columnRequestId", requestId);

        Column column = Column.builder().columnId(10L).build();
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
        RejectColumnRequestDto dto = new RejectColumnRequestDto();
        ReflectionTestUtils.setField(dto, "columnRequestId", requestId);
        ReflectionTestUtils.setField(dto, "reason", "부적절한 내용");

        Column column = Column.builder().columnId(20L).build();
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
}