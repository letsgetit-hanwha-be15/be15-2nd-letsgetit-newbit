package com.newbit.column.service;

import com.newbit.column.domain.Column;
import com.newbit.column.dto.response.GetColumnDetailResponseDto;
import com.newbit.column.dto.response.GetColumnListResponseDto;
import com.newbit.column.repository.ColumnRepository;
import com.newbit.common.exception.BusinessException;
import com.newbit.common.exception.ErrorCode;
import com.newbit.purchase.query.service.ColumnPurchaseHistoryQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ColumnServiceTest {

    @Mock
    private ColumnRepository columnRepository;

    @Mock
    private ColumnPurchaseHistoryQueryService columnPurchaseHistoryQueryService;

    @InjectMocks
    private ColumnService columnService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

//    public ColumnServiceTest() {
//        MockitoAnnotations.openMocks(this);
//    }

    @DisplayName("공개된 칼럼 상세 조회 성공")
    @Test
    void getPublicColumnDetail_success() {
        // given
        Long userId = 1L;
        Long columnId = 1L;
        Column column = Column.builder()
                .columnId(columnId)
                .title("테스트 제목")
                .content("테스트 내용")
                .price(1000)
                .thumbnailUrl("https://example.com/image.jpg")
                .likeCount(5)
                .mentorId(10L)
                .isPublic(true)
                .build();

        when(columnRepository.findById(columnId)).thenReturn(Optional.of(column));
        when(columnPurchaseHistoryQueryService.hasUserPurchasedColumn(userId, columnId)).thenReturn(true);

        // when
        GetColumnDetailResponseDto result = columnService.getColumnDetail(userId, columnId);

        // then
        assertThat(result.getColumnId()).isEqualTo(columnId);
        assertThat(result.getTitle()).isEqualTo("테스트 제목");
        assertThat(result.getContent()).isEqualTo("테스트 내용");
        assertThat(result.getPrice()).isEqualTo(1000);
        assertThat(result.getThumbnailUrl()).isEqualTo("https://example.com/image.jpg");
        assertThat(result.getLikeCount()).isEqualTo(5);
        assertThat(result.getMentorId()).isEqualTo(10L);
    }

    @DisplayName("비공개 칼럼일 경우 예외 발생")
    @Test
    void getPublicColumnDetail_notPublic_throwsException() {
        // given
        Long userId = 1L;
        Long columnId = 2L;
        Column column = Column.builder()
                .columnId(columnId)
                .isPublic(false)
                .build();

        when(columnRepository.findById(columnId)).thenReturn(Optional.of(column));

        // when & then
        assertThatThrownBy(() -> columnService.getColumnDetail(userId, columnId))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.COLUMN_NOT_FOUND.getMessage());
    }

    @DisplayName("칼럼이 존재하지 않을 경우 예외 발생")
    @Test
    void getPublicColumnDetail_notFound_throwsException() {
        // given
        Long userId = 1L;
        Long invalidId = 3L;
        when(columnRepository.findById(invalidId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> columnService.getColumnDetail(userId, invalidId))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.COLUMN_NOT_FOUND.getMessage());
    }

    @DisplayName("칼럼을 구매하지 않은 경우 예외 발생")
    @Test
    void getPublicColumnDetail_notPurchased_throwsException() {
        // given
        Long userId = 1L;
        Long columnId = 4L;
        Column column = Column.builder()
                .columnId(columnId)
                .isPublic(true)
                .build();

        when(columnRepository.findById(columnId)).thenReturn(Optional.of(column));
        when(columnPurchaseHistoryQueryService.hasUserPurchasedColumn(userId, columnId)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> columnService.getColumnDetail(userId, columnId))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.COLUMN_NOT_PURCHASED.getMessage());
    }

    @DisplayName("공개된 칼럼 목록 조회 - 페이징 적용")
    @Test
    void getPublicColumnList_paging_success() {
        // given
        int page = 0;
        int size = 2;
        Pageable pageable = PageRequest.of(page, size);

        Column column1 = Column.builder()
                .columnId(1L)
                .title("이직을 위한 포트폴리오 전략")
                .thumbnailUrl("https://example.com/img1.jpg")
                .price(1000)
                .likeCount(12)
                .mentorId(101L)
                .isPublic(true)
                .build();

        Column column2 = Column.builder()
                .columnId(2L)
                .title("개발자 연봉 협상법")
                .thumbnailUrl("https://example.com/img2.jpg")
                .price(2000)
                .likeCount(20)
                .mentorId(102L)
                .isPublic(true)
                .build();

        List<Column> columnList = List.of(column1, column2);
        Page<Column> columnPage = new PageImpl<>(columnList, pageable, columnList.size());

        when(columnRepository.findAllByIsPublicTrueOrderByCreatedAtDesc(pageable)).thenReturn(columnPage);

        // when
        Page<GetColumnListResponseDto> result = columnService.getPublicColumnList(page, size);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(2);

        GetColumnListResponseDto dto1 = result.getContent().get(0);
        assertThat(dto1.getColumnId()).isEqualTo(1L);
        assertThat(dto1.getTitle()).isEqualTo("이직을 위한 포트폴리오 전략");

        GetColumnListResponseDto dto2 = result.getContent().get(1);
        assertThat(dto2.getColumnId()).isEqualTo(2L);
        assertThat(dto2.getTitle()).isEqualTo("개발자 연봉 협상법");
    }
}
