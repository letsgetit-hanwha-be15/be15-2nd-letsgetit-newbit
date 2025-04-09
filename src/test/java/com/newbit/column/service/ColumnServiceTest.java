package com.newbit.column.service;

import com.newbit.column.domain.Column;
import com.newbit.column.dto.response.GetColumnDetailResponseDto;
import com.newbit.column.repository.ColumnRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ColumnServiceTest {

    @Mock
    private ColumnRepository columnRepository;

    @InjectMocks
    private ColumnService columnService;

    public ColumnServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("공개된 칼럼 상세 조회 성공")
    @Test
    void getPublicColumnDetail_success() {
        // given
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

        // when
        GetColumnDetailResponseDto result = columnService.getColumnDetail(columnId);

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
        Long columnId = 2L;
        Column column = Column.builder()
                .columnId(columnId)
                .isPublic(false)
                .build();

        when(columnRepository.findById(columnId)).thenReturn(Optional.of(column));

        // when & then
        assertThatThrownBy(() -> columnService.getColumnDetail(columnId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("공개된 칼럼이 아닙니다.");
    }

    @DisplayName("칼럼이 존재하지 않을 경우 예외 발생")
    @Test
    void getPublicColumnDetail_notFound_throwsException() {
        // given
        Long invalidId = 3L;
        when(columnRepository.findById(invalidId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> columnService.getColumnDetail(invalidId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("해당 칼럼을 찾을 수 없습니다. columnId = " + invalidId);
    }
}
