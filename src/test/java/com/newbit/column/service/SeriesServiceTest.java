package com.newbit.column.service;

import com.newbit.column.domain.Column;
import com.newbit.column.domain.Series;
import com.newbit.column.dto.request.CreateSeriesRequestDto;
import com.newbit.column.dto.request.UpdateSeriesRequestDto;
import com.newbit.column.dto.response.CreateSeriesResponseDto;
import com.newbit.column.mapper.SeriesMapper;
import com.newbit.column.repository.ColumnRepository;
import com.newbit.column.repository.SeriesRepository;
import com.newbit.user.entity.Mentor;
import com.newbit.user.service.MentorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class SeriesServiceTest {

    @Mock
    private SeriesRepository seriesRepository;

    @Mock
    private ColumnRepository columnRepository;

    @Mock
    private MentorService mentorService;

    @Mock
    private SeriesMapper seriesMapper;

    @InjectMocks
    private SeriesService seriesService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("시리즈 생성 - 성공")
    @Test
    void createSeries_success() {
        // given
        Long userId = 1L;
        Long mentorId = 10L;

        CreateSeriesRequestDto dto = new CreateSeriesRequestDto(
                "시리즈 제목",
                "시리즈 설명",
                "https://example.com/img.jpg",
                List.of(1L, 2L)
        );

        Mentor mentor = Mentor.builder().mentorId(mentorId).build();

        Column column1 = Column.builder().columnId(1L).mentor(mentor).build();
        Column column2 = Column.builder().columnId(2L).mentor(mentor).build();
        List<Column> columns = List.of(column1, column2);

        Series series = Series.builder().seriesId(100L).title(dto.getTitle()).build();
        CreateSeriesResponseDto responseDto = CreateSeriesResponseDto.builder().seriesId(100L).build();

        when(mentorService.getMentorEntityByUserId(userId)).thenReturn(mentor);
        when(columnRepository.findAllById(dto.getColumnIds())).thenReturn(columns);
        when(seriesMapper.toSeries(dto)).thenReturn(series);
        when(seriesRepository.save(series)).thenReturn(series);
        when(seriesMapper.toCreateSeriesResponseDto(series)).thenReturn(responseDto);

        // when
        CreateSeriesResponseDto result = seriesService.createSeries(dto, userId);

        // then
        assertThat(result.getSeriesId()).isEqualTo(100L);
        verify(mentorService).getMentorEntityByUserId(userId);
        verify(columnRepository).findAllById(dto.getColumnIds());
        verify(seriesRepository).save(series);
        verify(columnRepository).saveAll(columns);
        verify(seriesMapper).toCreateSeriesResponseDto(series);
    }

    @DisplayName("시리즈 수정 - 성공")
    @Test
    void updateSeries_success() {
        // given
        Long userId = 1L;
        Long mentorId = 10L;
        Long seriesId = 100L;

        Mentor mentor = Mentor.builder().mentorId(mentorId).build();

        Column oldColumn1 = Column.builder().columnId(1L).mentor(mentor).build();
        Column oldColumn2 = Column.builder().columnId(2L).mentor(mentor).build();
        List<Column> existingColumns = List.of(oldColumn1, oldColumn2);

        Column newColumn1 = Column.builder().columnId(3L).mentor(mentor).build();
        Column newColumn2 = Column.builder().columnId(4L).mentor(mentor).build();
        List<Column> newColumns = List.of(newColumn1, newColumn2);

        Series series = Series.builder().seriesId(seriesId).title("기존 제목").description("기존 설명").build();

        UpdateSeriesRequestDto dto = new UpdateSeriesRequestDto(
                "수정된 제목",
                "수정된 설명",
                "https://example.com/updated.jpg",
                List.of(3L, 4L)
        );

        when(mentorService.getMentorEntityByUserId(userId)).thenReturn(mentor);
        when(seriesRepository.findById(seriesId)).thenReturn(java.util.Optional.of(series));
        when(columnRepository.findAllById(dto.getColumnIds())).thenReturn(newColumns);
        when(columnRepository.findAllBySeries_SeriesId(seriesId)).thenReturn(existingColumns);

        // when
        seriesService.updateSeries(seriesId, dto, userId);

        // then
        verify(mentorService).getMentorEntityByUserId(userId);
        verify(seriesRepository).findById(seriesId);
        verify(columnRepository).findAllById(dto.getColumnIds());
        verify(columnRepository).findAllBySeries_SeriesId(seriesId);
        verify(columnRepository).saveAll(existingColumns);
        verify(columnRepository).saveAll(newColumns);

        assertThat(series.getTitle()).isEqualTo("수정된 제목");
        assertThat(series.getDescription()).isEqualTo("수정된 설명");
        assertThat(series.getThumbnailUrl()).isEqualTo("https://example.com/updated.jpg");
    }

}
