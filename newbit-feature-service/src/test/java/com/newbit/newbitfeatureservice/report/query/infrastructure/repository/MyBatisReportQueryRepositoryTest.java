package com.newbit.newbitfeatureservice.report.query.infrastructure.repository;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.anyMap;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.newbit.newbitfeatureservice.report.command.domain.aggregate.ReportStatus;
import com.newbit.newbitfeatureservice.report.query.dto.response.ReportDTO;
import com.newbit.newbitfeatureservice.report.query.infrastructure.mapper.ReportMapper;

@ExtendWith(MockitoExtension.class)
class MyBatisReportQueryRepositoryTest {

    @Mock
    private ReportMapper reportMapper;

    @InjectMocks
    private MyBatisReportQueryRepository repository;

    @Captor
    private ArgumentCaptor<Map<String, Object>> paramCaptor;

    private List<ReportDTO> mockReports;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        // ReportDTO - 클래스 구조에 맞게 생성 방식 수정 
        ReportDTO report1 = new ReportDTO();
        ReportDTO report2 = new ReportDTO();
        
        // 페이징 객체 생성
        pageable = PageRequest.of(0, 10);
        
        mockReports = Arrays.asList(report1, report2);
    }

    @Test
    @DisplayName("신고 상태별 조회 시 올바른 파라미터가 전달되어야 한다")
    void findReportsByStatus() {
        // given
        when(reportMapper.findReports(anyMap())).thenReturn(mockReports);
        when(reportMapper.countReports(anyMap())).thenReturn(mockReports.size());
        
        // when
        Page<ReportDTO> result = repository.findReports(ReportStatus.PENDING, pageable);
        
        // then
        verify(reportMapper).findReports(paramCaptor.capture());
        Map<String, Object> params = paramCaptor.getValue();
        
        assertThat(params).containsEntry("status", ReportStatus.PENDING);
        assertThat(params).containsEntry("offset", 0L);
        assertThat(params).containsEntry("pageSize", 10);
        
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
    }
    
    @Test
    @DisplayName("게시글 ID로 조회 시 올바른 파라미터가 전달되어야 한다")
    void findReportsByPostId() {
        // given
        when(reportMapper.findReports(anyMap())).thenReturn(mockReports);
        when(reportMapper.countReports(anyMap())).thenReturn(mockReports.size());
        
        // when
        repository.findReportsByPostId(200L, pageable);
        
        // then
        verify(reportMapper).findReports(paramCaptor.capture());
        Map<String, Object> params = paramCaptor.getValue();
        
        assertThat(params).containsEntry("postId", 200L);
        assertThat(params).containsEntry("offset", 0L);
        assertThat(params).containsEntry("pageSize", 10);
        
        assertThat(mockReports).hasSize(2);
    }
    
    @Test
    @DisplayName("신고 상태와 유형으로 조회 시 올바른 파라미터가 전달되어야 한다")
    void findReportsByStatusAndReportTypeId() {
        // given
        when(reportMapper.findReports(anyMap())).thenReturn(mockReports);
        when(reportMapper.countReports(anyMap())).thenReturn(mockReports.size());
        
        // when
        repository.findReportsByStatusAndReportTypeId(ReportStatus.PENDING, 1L, pageable);
        
        // then
        verify(reportMapper).findReports(paramCaptor.capture());
        Map<String, Object> params = paramCaptor.getValue();
        
        assertThat(params).containsEntry("status", ReportStatus.PENDING);
        assertThat(params).containsEntry("reportTypeId", 1L);
        assertThat(params).containsEntry("offset", 0L);
        assertThat(params).containsEntry("pageSize", 10);
    }
    
    @Test
    @DisplayName("콘텐츠 작성자 ID로 조회 시 올바른 파라미터가 전달되어야 한다")
    void findReportsByContentUserId() {
        // given
        when(reportMapper.findReports(anyMap())).thenReturn(mockReports);
        when(reportMapper.countReports(anyMap())).thenReturn(mockReports.size());
        
        // when
        repository.findReportsByContentUserId(500L, pageable);
        
        // then
        verify(reportMapper).findReports(paramCaptor.capture());
        Map<String, Object> params = paramCaptor.getValue();
        
        assertThat(params).containsEntry("contentUserId", 500L);
        assertThat(params).containsEntry("offset", 0L);
        assertThat(params).containsEntry("pageSize", 10);
    }
} 