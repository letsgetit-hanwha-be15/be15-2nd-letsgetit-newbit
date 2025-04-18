package com.newbit.newbitfeatureservice.column.service;

import com.newbit.newbitfeatureservice.client.user.MentorFeignClient;
import com.newbit.newbitfeatureservice.client.user.UserFeignClient;
import com.newbit.newbitfeatureservice.column.domain.Column;
import com.newbit.newbitfeatureservice.column.dto.response.GetColumnDetailResponseDto;
import com.newbit.newbitfeatureservice.column.dto.response.GetColumnListResponseDto;
import com.newbit.newbitfeatureservice.column.dto.response.GetMyColumnListResponseDto;
import com.newbit.newbitfeatureservice.column.mapper.ColumnMapper;
import com.newbit.newbitfeatureservice.column.repository.ColumnRepository;
import com.newbit.newbitfeatureservice.common.dto.ApiResponse;
import com.newbit.newbitfeatureservice.common.exception.BusinessException;
import com.newbit.newbitfeatureservice.common.exception.ErrorCode;
import com.newbit.newbitfeatureservice.purchase.query.service.ColumnPurchaseHistoryQueryService;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ColumnServiceTest {

    @Mock
    private ColumnRepository columnRepository;

    @Mock
    private ColumnPurchaseHistoryQueryService columnPurchaseHistoryQueryService;

    @Mock
    private MentorFeignClient mentorFeignClient;

    @Mock
    private ColumnMapper columnMapper;

    @InjectMocks
    private ColumnService columnService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @DisplayName("공개된 칼럼 상세 조회 성공")
    @Test
    void getPublicColumnDetail_success() {
        // given
        Long userId = 1L;
        Long columnId = 1L;
        Long mentorId = 10L;
        String nickname = "개발자도토리";

        GetColumnDetailResponseDto responseDto = new GetColumnDetailResponseDto(
                columnId,
                "테스트 제목",
                "테스트 내용",
                1000,
                "https://example.com/image.jpg",
                5,
                mentorId
        );
        responseDto.setMentorNickname(nickname);

        when(columnRepository.findPublicColumnDetailById(columnId)).thenReturn(Optional.of(responseDto));
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
        assertThat(result.getMentorNickname()).isEqualTo("개발자도토리");
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
        Long mentorId = 10L;
        String nickname = "개발자도토리";

        GetColumnDetailResponseDto responseDto = new GetColumnDetailResponseDto(
                columnId,
                "테스트 제목",
                "테스트 내용",
                1000,
                "https://example.com/image.jpg",
                5,
                 mentorId
        );
        responseDto.setMentorNickname(nickname);

        when(columnRepository.findPublicColumnDetailById(columnId)).thenReturn(Optional.of(responseDto));
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

        GetColumnListResponseDto dto1 = new GetColumnListResponseDto(
                1L, "이직을 위한 포트폴리오 전략", "https://example.com/img1.jpg", 1000, 12, 101L
        );
        dto1.setMentorNickname("개발자도토리");

        GetColumnListResponseDto dto2 = new GetColumnListResponseDto(
                2L, "개발자 연봉 협상법", "https://example.com/img2.jpg", 2000, 20, 102L
        );
        dto2.setMentorNickname("연봉왕");


        List<GetColumnListResponseDto> dtoList = List.of(dto1, dto2);
        Page<GetColumnListResponseDto> columnPage = new PageImpl<>(dtoList, pageable, dtoList.size());

        when(columnRepository.findAllByIsPublicTrueOrderByCreatedAtDesc(pageable)).thenReturn(columnPage);

        // when
        Page<GetColumnListResponseDto> result = columnService.getPublicColumnList(page, size);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(2);

        GetColumnListResponseDto resultDto1 = result.getContent().get(0);
        assertThat(resultDto1.getColumnId()).isEqualTo(1L);
        assertThat(resultDto1.getTitle()).isEqualTo("이직을 위한 포트폴리오 전략");
        assertThat(resultDto1.getMentorNickname()).isEqualTo("개발자도토리");

        GetColumnListResponseDto resultDto2 = result.getContent().get(1);
        assertThat(resultDto2.getColumnId()).isEqualTo(2L);
        assertThat(resultDto2.getTitle()).isEqualTo("개발자 연봉 협상법");
        assertThat(resultDto2.getMentorNickname()).isEqualTo("연봉왕");
    }

    @DisplayName("멘토 본인 칼럼 목록 조회 - 성공")
    @Test
    void getMyColumnList_success() {
        // given
        Long userId = 1L;
        Long mentorId = 10L;

        Column column1 = Column.builder()
                .columnId(1L)
                .title("멘토 칼럼 1")
                .thumbnailUrl("https://example.com/thumb1.jpg")
                .price(1000)
                .likeCount(5)
                .isPublic(true)
                .mentorId(mentorId)
                .build();

        Column column2 = Column.builder()
                .columnId(2L)
                .title("멘토 칼럼 2")
                .thumbnailUrl("https://example.com/thumb2.jpg")
                .price(1500)
                .likeCount(10)
                .isPublic(true)
                .mentorId(mentorId)
                .build();

        List<Column> columns = List.of(column1, column2);

        when(mentorFeignClient.getMentorIdByUserId(userId)).thenReturn(ApiResponse.success(mentorId));
        when(columnRepository.findAllByMentorIdAndIsPublicTrueOrderByCreatedAtDesc(mentorId)).thenReturn(columns);
        when(columnMapper.toMyColumnListDto(column1)).thenReturn(
                GetMyColumnListResponseDto.builder()
                        .columnId(1L)
                        .title("멘토 칼럼 1")
                        .thumbnailUrl("https://example.com/thumb1.jpg")
                        .price(1000)
                        .likeCount(5)
                        .build()
        );
        when(columnMapper.toMyColumnListDto(column2)).thenReturn(
                GetMyColumnListResponseDto.builder()
                        .columnId(2L)
                        .title("멘토 칼럼 2")
                        .thumbnailUrl("https://example.com/thumb2.jpg")
                        .price(1500)
                        .likeCount(10)
                        .build()
        );

        // when
        List<GetMyColumnListResponseDto> result = columnService.getMyColumnList(userId);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getColumnId()).isEqualTo(1L);
        assertThat(result.get(0).getTitle()).isEqualTo("멘토 칼럼 1");
        assertThat(result.get(1).getTitle()).isEqualTo("멘토 칼럼 2");

        verify(mentorFeignClient).getMentorIdByUserId(userId);
        verify(columnRepository).findAllByMentorIdAndIsPublicTrueOrderByCreatedAtDesc(mentorId);
        verify(columnMapper).toMyColumnListDto(column1);
        verify(columnMapper).toMyColumnListDto(column2);
    }

}
