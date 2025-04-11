package com.newbit.column.service;

import com.newbit.column.domain.Column;
import com.newbit.column.domain.Series;
import com.newbit.column.dto.request.CreateSeriesRequestDto;
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
}
