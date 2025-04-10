package com.newbit.report.command.application.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.newbit.post.entity.Post;
import com.newbit.post.repository.PostRepository;
import com.newbit.report.command.application.dto.request.ReportCreateRequest;
import com.newbit.report.command.application.dto.response.ReportCommandResponse;
import com.newbit.report.command.domain.aggregate.Report;
import com.newbit.report.command.domain.aggregate.ReportStatus;
import com.newbit.report.command.domain.aggregate.ReportType;
import com.newbit.report.command.domain.repository.ReportRepository;
import com.newbit.report.command.domain.repository.ReportTypeRepository;
import com.newbit.report.command.infrastructure.repository.JpaReportRepository;

@ExtendWith(MockitoExtension.class)
class ReportCommandServiceTest {

    @Mock
    private ReportRepository reportRepository;
    
    @Mock
    private ReportTypeRepository reportTypeRepository;
    
    @Mock
    private PostRepository postRepository;
    
    @Mock
    private JpaReportRepository jpaReportRepository;
    
    @InjectMocks
    private ReportCommandService reportCommandService;
    
    @Test
    @DisplayName("게시글 신고 생성 테스트")
    void createPostReportTest() {
        // Given
        Long userId = 1L;
        Long postId = 1L;
        Long reportTypeId = 1L;
        String content = "신고 내용";
        
        ReportCreateRequest postReportRequest = new ReportCreateRequest(userId, postId, reportTypeId, content);
        
        // ReportType 모킹
        ReportType reportType = mock(ReportType.class);
        when(reportType.getId()).thenReturn(reportTypeId);
        when(reportTypeRepository.findById(reportTypeId)).thenReturn(Optional.of(reportType));
        
        // Report 모킹
        Report report = mock(Report.class);
        when(report.getReportId()).thenReturn(1L);
        when(report.getUserId()).thenReturn(userId);
        when(report.getPostId()).thenReturn(postId);
        when(report.getReportType()).thenReturn(reportType);
        when(report.getContent()).thenReturn(content);
        when(report.getStatus()).thenReturn(ReportStatus.SUBMITTED);
        
        when(reportRepository.save(any(Report.class))).thenReturn(report);
        
        // Post 모킹
        Post post = Post.builder()
                .id(postId)
                .title("테스트 게시글")
                .content("테스트 내용")
                .userId(2L)
                .reportCount(0)
                .build();
        
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        
        // When
        ReportCommandResponse response = reportCommandService.createPostReport(postReportRequest);
        
        // Then
        assertNotNull(response);
        verify(reportRepository, times(1)).save(any(Report.class));
        verify(postRepository, times(1)).findById(postId);
    }
    
    @Test
    @DisplayName("신고 횟수 임계값(50) 도달 시 게시글 삭제 테스트")
    void reportThresholdTest() {
        // Given
        Long userId = 1L;
        Long postId = 1L;
        Long reportTypeId = 1L;
        String content = "신고 내용";
        
        ReportCreateRequest postReportRequest = new ReportCreateRequest(userId, postId, reportTypeId, content);
        
        // 임계값 직전의 게시글 설정
        Post postWithHighReportCount = Post.builder()
                .id(postId)
                .title("많은 신고를 받은 게시글")
                .content("신고가 많은 내용")
                .userId(2L)
                .reportCount(49) // 임계값 직전
                .build();
        
        // ReportType 모킹
        ReportType reportType = mock(ReportType.class);
        when(reportType.getId()).thenReturn(reportTypeId);
        when(reportTypeRepository.findById(reportTypeId)).thenReturn(Optional.of(reportType));
        
        // Report 모킹
        Report report = mock(Report.class);
        when(report.getReportId()).thenReturn(1L);
        when(report.getUserId()).thenReturn(userId);
        when(report.getPostId()).thenReturn(postId);
        when(report.getReportType()).thenReturn(reportType);
        when(report.getContent()).thenReturn(content);
        when(report.getStatus()).thenReturn(ReportStatus.SUBMITTED);
        
        when(reportRepository.save(any(Report.class))).thenReturn(report);
        when(postRepository.findById(postId)).thenReturn(Optional.of(postWithHighReportCount));
        
        // 관련 신고 리스트 모킹
        Report mockReport1 = mock(Report.class);
        Report mockReport2 = mock(Report.class);
        List<Report> reportList = Arrays.asList(mockReport1, mockReport2);
        when(jpaReportRepository.findAllByPostId(postId)).thenReturn(reportList);
        
        // When
        ReportCommandResponse response = reportCommandService.createPostReport(postReportRequest);
        
        // Then
        assertNotNull(response);
        assertEquals(50, postWithHighReportCount.getReportCount()); // 50개로 증가
        assertNotNull(postWithHighReportCount.getDeletedAt()); // 삭제일시 설정됨
        verify(jpaReportRepository, times(1)).findAllByPostId(postId);
    }
    
    @Test
    @DisplayName("관리자가 신고를 처리하는 테스트")
    void processReportTest() {
        // Given
        Long reportId = 1L;
        
        // Report 모킹
        Report report = mock(Report.class);
        when(report.getReportId()).thenReturn(reportId);
        when(reportRepository.findById(reportId)).thenReturn(report);
        
        // When
        ReportCommandResponse response = reportCommandService.processReport(reportId, ReportStatus.NOT_VIOLATED);
        
        // Then
        assertNotNull(response);
        verify(report, times(1)).updateStatus(ReportStatus.NOT_VIOLATED);
        verify(reportRepository, times(1)).findById(reportId);
    }
    
    @Test
    @DisplayName("관리자가 신고를 처리하고 게시글 삭제 결정 테스트")
    void processReportAndDeletePostTest() {
        // Given
        Long reportId = 1L;
        Long postId = 2L;
        
        // Report 모킹
        Report report = mock(Report.class);
        when(report.getReportId()).thenReturn(reportId);
        when(report.getPostId()).thenReturn(postId);
        when(reportRepository.findById(reportId)).thenReturn(report);
        
        // Post 모킹
        Post post = Post.builder()
                .id(postId)
                .title("테스트 게시글")
                .content("테스트 내용")
                .userId(2L)
                .build();
        
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        
        // When
        ReportCommandResponse response = reportCommandService.processReport(reportId, ReportStatus.DELETED);
        
        // Then
        assertNotNull(response);
        verify(report, times(1)).updateStatus(ReportStatus.DELETED);
        assertNotNull(post.getDeletedAt()); // 삭제일시 설정됨
        verify(reportRepository, times(1)).findById(reportId);
        verify(postRepository, times(1)).findById(postId);
    }
} 