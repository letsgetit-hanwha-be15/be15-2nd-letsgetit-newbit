package com.newbit.newbitfeatureservice.column.service;

import com.newbit.newbitfeatureservice.client.user.MentorFeignClient;
import com.newbit.newbitfeatureservice.column.domain.Column;
import com.newbit.newbitfeatureservice.column.domain.Series;
import com.newbit.newbitfeatureservice.column.dto.request.CreateSeriesRequestDto;
import com.newbit.newbitfeatureservice.column.dto.request.UpdateSeriesRequestDto;
import com.newbit.newbitfeatureservice.column.dto.response.CreateSeriesResponseDto;
import com.newbit.newbitfeatureservice.column.dto.response.GetMySeriesListResponseDto;
import com.newbit.newbitfeatureservice.column.dto.response.GetSeriesColumnsResponseDto;
import com.newbit.newbitfeatureservice.column.dto.response.GetSeriesDetailResponseDto;
import com.newbit.newbitfeatureservice.column.mapper.SeriesMapper;
import com.newbit.newbitfeatureservice.column.repository.ColumnRepository;
import com.newbit.newbitfeatureservice.column.repository.SeriesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class SeriesServiceTest {

    @Mock
    private SeriesRepository seriesRepository;

    @Mock
    private ColumnRepository columnRepository;

    @Mock
    private MentorFeignClient mentorFeignClient;

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

        Column column1 = Column.builder().columnId(1L).mentorId(mentorId).build();
        Column column2 = Column.builder().columnId(2L).mentorId(mentorId).build();
        List<Column> columns = List.of(column1, column2);

        Series series = Series.builder().seriesId(100L).title(dto.getTitle()).build();
        CreateSeriesResponseDto responseDto = CreateSeriesResponseDto.builder().seriesId(100L).build();

        when(mentorFeignClient.getMentorIdByUserId(userId).getData()).thenReturn(mentorId);
        when(columnRepository.findAllById(dto.getColumnIds())).thenReturn(columns);
        when(seriesMapper.toSeries(dto)).thenReturn(series);
        when(seriesRepository.save(series)).thenReturn(series);
        when(seriesMapper.toCreateSeriesResponseDto(series)).thenReturn(responseDto);

        // when
        CreateSeriesResponseDto result = seriesService.createSeries(dto, userId);

        // then
        assertThat(result.getSeriesId()).isEqualTo(100L);
        verify(mentorFeignClient).getMentorIdByUserId(userId);
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

        Column oldColumn1 = Column.builder().columnId(1L).mentorId(mentorId).build();
        Column oldColumn2 = Column.builder().columnId(2L).mentorId(mentorId).build();
        List<Column> existingColumns = List.of(oldColumn1, oldColumn2);

        Column newColumn1 = Column.builder().columnId(3L).mentorId(mentorId).build();
        Column newColumn2 = Column.builder().columnId(4L).mentorId(mentorId).build();
        List<Column> newColumns = List.of(newColumn1, newColumn2);

        Series series = Series.builder().seriesId(seriesId).title("기존 제목").description("기존 설명").build();

        UpdateSeriesRequestDto dto = new UpdateSeriesRequestDto(
                "수정된 제목",
                "수정된 설명",
                "https://example.com/updated.jpg",
                List.of(3L, 4L)
        );

        when(mentorFeignClient.getMentorIdByUserId(userId).getData()).thenReturn(mentorId);
        when(seriesRepository.findById(seriesId)).thenReturn(Optional.of(series));
        when(columnRepository.findAllById(dto.getColumnIds())).thenReturn(newColumns);
        when(columnRepository.findAllBySeries_SeriesId(seriesId)).thenReturn(existingColumns);

        // when
        seriesService.updateSeries(seriesId, dto, userId);

        // then
        verify(mentorFeignClient).getMentorIdByUserId(userId);
        verify(seriesRepository).findById(seriesId);
        verify(columnRepository).findAllById(dto.getColumnIds());
        verify(columnRepository).findAllBySeries_SeriesId(seriesId);
        verify(columnRepository).saveAll(existingColumns);
        verify(columnRepository).saveAll(newColumns);

        assertThat(series.getTitle()).isEqualTo("수정된 제목");
        assertThat(series.getDescription()).isEqualTo("수정된 설명");
        assertThat(series.getThumbnailUrl()).isEqualTo("https://example.com/updated.jpg");
    }

    @DisplayName("시리즈 삭제 - 성공")
    @Test
    void deleteSeries_success() {
        // given
        Long seriesId = 100L;
        Long userId = 1L;
        Long mentorId = 10L;

        Series series = Series.builder().seriesId(seriesId).build();

        Column column1 = Column.builder().columnId(1L).mentorId(mentorId).series(series).build();
        Column column2 = Column.builder().columnId(2L).mentorId(mentorId).series(series).build();
        List<Column> columns = List.of(column1, column2);

        when(mentorFeignClient.getMentorIdByUserId(userId).getData()).thenReturn(mentorId);
        when(seriesRepository.findById(seriesId)).thenReturn(Optional.of(series));
        when(columnRepository.findAllBySeries_SeriesId(seriesId)).thenReturn(columns);

        // when
        seriesService.deleteSeries(seriesId, userId);

        // then
        verify(mentorFeignClient).getMentorIdByUserId(userId);
        verify(seriesRepository).findById(seriesId);
        verify(columnRepository).findAllBySeries_SeriesId(seriesId);
        verify(columnRepository).saveAll(anyList());
        verify(seriesRepository).delete(series);
    }

    @DisplayName("시리즈 상세 조회 - 성공")
    @Test
    void getSeriesDetail_success() {
        // given
        Long seriesId = 1L;

        Series series = Series.builder()
                .seriesId(seriesId)
                .title("이직 준비 A to Z")
                .description("이직을 준비하는 멘티들을 위한 시리즈입니다.")
                .thumbnailUrl("https://example.com/image.jpg")
                .build();

        GetSeriesDetailResponseDto expectedDto = GetSeriesDetailResponseDto.builder()
                .seriesId(seriesId)
                .title("이직 준비 A to Z")
                .description("이직을 준비하는 멘티들을 위한 시리즈입니다.")
                .thumbnailUrl("https://example.com/image.jpg")
                .build();

        when(seriesRepository.findById(seriesId)).thenReturn(Optional.of(series));
        when(seriesMapper.toGetSeriesDetailResponseDto(series)).thenReturn(expectedDto);

        // when
        GetSeriesDetailResponseDto result = seriesService.getSeriesDetail(seriesId);

        // then
        assertThat(result.getSeriesId()).isEqualTo(seriesId);
        assertThat(result.getTitle()).isEqualTo("이직 준비 A to Z");
        assertThat(result.getDescription()).isEqualTo("이직을 준비하는 멘티들을 위한 시리즈입니다.");
        assertThat(result.getThumbnailUrl()).isEqualTo("https://example.com/image.jpg");

        verify(seriesRepository).findById(seriesId);
        verify(seriesMapper).toGetSeriesDetailResponseDto(series);
    }

    @DisplayName("멘토 본인 시리즈 목록 조회 - 성공")
    @Test
    void getMySeriesList_success() {
        // given
        Long userId = 1L;
        Long mentorId = 10L;

        Series series1 = Series.builder()
                .seriesId(1L)
                .title("시리즈 A")
                .description("설명 A")
                .thumbnailUrl("https://example.com/a.jpg")
                .build();

        Series series2 = Series.builder()
                .seriesId(2L)
                .title("시리즈 B")
                .description("설명 B")
                .thumbnailUrl("https://example.com/b.jpg")
                .build();

        List<Series> seriesList = List.of(series1, series2);

        when(mentorFeignClient.getMentorIdByUserId(userId).getData()).thenReturn(mentorId);
        when(seriesRepository.findAllByMentorIdOrderByCreatedAtDesc(mentorId)).thenReturn(seriesList);
        when(seriesMapper.toMySeriesListDto(series1)).thenReturn(
                GetMySeriesListResponseDto.builder()
                        .seriesId(1L)
                        .title("시리즈 A")
                        .description("설명 A")
                        .thumbnailUrl("https://example.com/a.jpg")
                        .build()
        );
        when(seriesMapper.toMySeriesListDto(series2)).thenReturn(
                GetMySeriesListResponseDto.builder()
                        .seriesId(2L)
                        .title("시리즈 B")
                        .description("설명 B")
                        .thumbnailUrl("https://example.com/b.jpg")
                        .build()
        );

        // when
        List<GetMySeriesListResponseDto> result = seriesService.getMySeriesList(userId);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getSeriesId()).isEqualTo(1L);
        assertThat(result.get(0).getTitle()).isEqualTo("시리즈 A");
        assertThat(result.get(1).getSeriesId()).isEqualTo(2L);
        assertThat(result.get(1).getTitle()).isEqualTo("시리즈 B");

        verify(mentorFeignClient).getMentorIdByUserId(userId);
        verify(seriesRepository).findAllByMentorIdOrderByCreatedAtDesc(mentorId);
        verify(seriesMapper).toMySeriesListDto(series1);
        verify(seriesMapper).toMySeriesListDto(series2);
    }

    @DisplayName("시리즈 내 포함된 칼럼 목록 조회 - 성공")
    @Test
    void getColumnsInSeries_success() {
        // given
        Long seriesId = 1L;

        Series series = Series.builder()
                .seriesId(seriesId)
                .title("이직 준비 시리즈")
                .build();

        Column column1 = Column.builder()
                .columnId(101L)
                .title("자기소개서 작성법")
                .price(1000)
                .thumbnailUrl("https://example.com/thumb1.jpg")
                .likeCount(10)
                .createdAt(LocalDateTime.now())
                .series(series)
                .build();

        Column column2 = Column.builder()
                .columnId(102L)
                .title("면접 꿀팁")
                .price(2000)
                .thumbnailUrl("https://example.com/thumb2.jpg")
                .likeCount(20)
                .createdAt(LocalDateTime.now())
                .series(series)
                .build();

        List<Column> columns = List.of(column1, column2);

        when(seriesRepository.findById(seriesId)).thenReturn(Optional.of(series));
        when(columnRepository.findAllBySeries_SeriesId(seriesId)).thenReturn(columns);

        when(seriesMapper.toSeriesColumnDto(column1)).thenReturn(
                GetSeriesColumnsResponseDto.builder()
                        .columnId(101L)
                        .title("자기소개서 작성법")
                        .price(1000)
                        .thumbnailUrl("https://example.com/thumb1.jpg")
                        .likeCount(10)
                        .createdAt(column1.getCreatedAt())
                        .updatedAt(column1.getUpdatedAt())
                        .build()
        );

        when(seriesMapper.toSeriesColumnDto(column2)).thenReturn(
                GetSeriesColumnsResponseDto.builder()
                        .columnId(102L)
                        .title("면접 꿀팁")
                        .price(2000)
                        .thumbnailUrl("https://example.com/thumb2.jpg")
                        .likeCount(20)
                        .createdAt(column2.getCreatedAt())
                        .updatedAt(column2.getUpdatedAt())
                        .build()
        );

        // when
        List<GetSeriesColumnsResponseDto> result = seriesService.getSeriesColumns(seriesId);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getColumnId()).isEqualTo(101L);
        assertThat(result.get(1).getTitle()).isEqualTo("면접 꿀팁");

        verify(columnRepository).findAllBySeries_SeriesId(seriesId);
        verify(seriesMapper).toSeriesColumnDto(column1);
        verify(seriesMapper).toSeriesColumnDto(column2);
    }

}
