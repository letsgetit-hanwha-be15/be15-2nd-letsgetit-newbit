package com.newbit.report.command.application.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.newbit.post.repository.CommentRepository;
import com.newbit.post.repository.PostRepository;
import com.newbit.post.service.CommentService;
import com.newbit.post.service.PostService;
import com.newbit.report.command.application.dto.request.ReportCreateRequest;
import com.newbit.report.command.application.dto.response.ReportCommandResponse;
import com.newbit.report.command.domain.aggregate.Report;
import com.newbit.report.command.domain.aggregate.ReportStatus;
import com.newbit.report.command.domain.aggregate.ReportType;
import com.newbit.report.command.domain.repository.ReportRepository;
import com.newbit.report.command.domain.repository.ReportTypeRepository;

@ExtendWith(MockitoExtension.class)
class ReportCommandServiceTest {

    @Mock
    private ReportRepository reportRepository;
    
    @Mock
    private ReportTypeRepository reportTypeRepository;
    
    @Mock
    private PostRepository postRepository;
    
    @Mock
    private CommentRepository commentRepository;
    
    @Mock
    private PostService postService;
    
    @Mock
    private CommentService commentService;
    
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
        
        ReportCreateRequest postReportRequest = ReportCreateRequest.forPost(userId, postId, reportTypeId, content);
        
        // ReportType 모킹
        ReportType reportType = mock(ReportType.class);
        when(reportTypeRepository.findById(reportTypeId)).thenReturn(Optional.of(reportType));
        
        // Report 모킹
        Report report = mock(Report.class);
        when(reportRepository.save(any(Report.class))).thenReturn(report);
        
        // PostService 모킹
        when(postService.getReportCountByPostId(postId)).thenReturn(1);
        
        // When
        ReportCommandResponse response = reportCommandService.createPostReport(postReportRequest);
        
        // Then
        assertNotNull(response);
        verify(reportRepository, times(1)).save(any(Report.class));
        verify(postService, times(1)).increaseReportCount(postId);
        verify(postService, times(1)).getReportCountByPostId(postId);
    }
    
    @Test
    @DisplayName("신고 횟수 임계값(50) 도달 시 게시글 삭제 테스트")
    void reportThresholdTest() {
        // Given
        Long userId = 1L;
        Long postId = 1L;
        Long reportTypeId = 1L;
        String content = "신고 내용";
        
        ReportCreateRequest postReportRequest = ReportCreateRequest.forPost(userId, postId, reportTypeId, content);
        
        // ReportType 모킹
        ReportType reportType = mock(ReportType.class);
        when(reportTypeRepository.findById(reportTypeId)).thenReturn(Optional.of(reportType));
        
        // Report 모킹
        Report report = mock(Report.class);
        when(reportRepository.save(any(Report.class))).thenReturn(report);
        
        // PostService 모킹 - 임계값에 도달하도록 설정
        when(postService.getReportCountByPostId(postId)).thenReturn(50);
        
        // 관련 신고 리스트 모킹
        Report mockReport1 = mock(Report.class);
        Report mockReport2 = mock(Report.class);
        List<Report> reportList = Arrays.asList(mockReport1, mockReport2);
        when(reportRepository.findAllByPostId(postId)).thenReturn(reportList);
        
        // When
        ReportCommandResponse response = reportCommandService.createPostReport(postReportRequest);
        
        // Then
        assertNotNull(response);
        verify(reportRepository, times(1)).save(any(Report.class));
        verify(postService, times(1)).increaseReportCount(postId);
        verify(postService, times(1)).getReportCountByPostId(postId);
        verify(postService, times(1)).deletePostAsAdmin(postId);
        verify(reportRepository, times(1)).findAllByPostId(postId);
        verify(mockReport1, times(1)).updateStatus(ReportStatus.DELETED);
        verify(mockReport2, times(1)).updateStatus(ReportStatus.DELETED);
    }
    
    @Test
    @DisplayName("관리자가 신고를 처리하는 테스트")
    void processReportTest() {
        // Given
        Long reportId = 1L;
        
        // Report 모킹
        Report report = mock(Report.class);
        when(reportRepository.findById(reportId)).thenReturn(Optional.of(report));
        
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
        when(report.getCommentId()).thenReturn(null);
        when(reportRepository.findById(reportId)).thenReturn(Optional.of(report));
        
        // When
        ReportCommandResponse response = reportCommandService.processReport(reportId, ReportStatus.DELETED);
        
        // Then
        assertNotNull(response);
        verify(report, times(1)).updateStatus(ReportStatus.DELETED);
        verify(reportRepository, times(1)).findById(reportId);
        verify(postService, times(1)).deletePostAsAdmin(postId);
    }
    
    @Test
    @DisplayName("댓글 신고 생성 테스트")
    void createCommentReportTest() {
        // Given
        Long userId = 1L;
        Long commentId = 1L;
        Long reportTypeId = 1L;
        String content = "댓글 신고 내용";
        
        ReportCreateRequest commentReportRequest = ReportCreateRequest.forComment(userId, commentId, reportTypeId, content);
        
        // ReportType 모킹
        ReportType reportType = mock(ReportType.class);
        when(reportTypeRepository.findById(reportTypeId)).thenReturn(Optional.of(reportType));
        
        // Report 모킹
        Report report = mock(Report.class);
        when(reportRepository.save(any(Report.class))).thenReturn(report);
        
        // CommentService 모킹
        when(commentService.getReportCount(commentId)).thenReturn(1);
        
        // When
        ReportCommandResponse response = reportCommandService.createCommentReport(commentReportRequest);
        
        // Then
        assertNotNull(response);
        verify(reportRepository, times(1)).save(any(Report.class));
        verify(commentService, times(1)).increaseReportCount(commentId);
        verify(commentService, times(1)).getReportCount(commentId);
    }
    
    @Test
    @DisplayName("댓글 신고 횟수 임계값(50) 도달 시 댓글 삭제 테스트")
    void commentReportThresholdTest() {
        // Given
        Long userId = 1L;
        Long commentId = 1L;
        Long reportTypeId = 1L;
        String content = "댓글 신고 내용";
        
        ReportCreateRequest commentReportRequest = ReportCreateRequest.forComment(userId, commentId, reportTypeId, content);
        
        // ReportType 모킹
        ReportType reportType = mock(ReportType.class);
        when(reportTypeRepository.findById(reportTypeId)).thenReturn(Optional.of(reportType));
        
        // Report 모킹
        Report report = mock(Report.class);
        when(reportRepository.save(any(Report.class))).thenReturn(report);
        
        // CommentService 모킹 - 임계값에 도달하도록 설정
        when(commentService.getReportCount(commentId)).thenReturn(50);
        
        // 관련 신고 리스트 모킹
        Report mockReport1 = mock(Report.class);
        Report mockReport2 = mock(Report.class);
        List<Report> reportList = Arrays.asList(mockReport1, mockReport2);
        when(reportRepository.findAllByCommentId(commentId)).thenReturn(reportList);
        
        // When
        ReportCommandResponse response = reportCommandService.createCommentReport(commentReportRequest);
        
        // Then
        assertNotNull(response);
        verify(reportRepository, times(1)).save(any(Report.class));
        verify(commentService, times(1)).increaseReportCount(commentId);
        verify(commentService, times(1)).getReportCount(commentId);
        verify(commentService, times(1)).deleteCommentAsAdmin(commentId);
        verify(reportRepository, times(1)).findAllByCommentId(commentId);
        verify(mockReport1, times(1)).updateStatus(ReportStatus.DELETED);
        verify(mockReport2, times(1)).updateStatus(ReportStatus.DELETED);
    }
    
    @Test
    @DisplayName("관리자가 댓글 신고를 처리하고 삭제 결정 테스트")
    void processReportAndDeleteCommentTest() {
        // Given
        Long reportId = 1L;
        Long commentId = 2L;
        
        // Report 모킹
        Report report = mock(Report.class);
        when(report.getReportId()).thenReturn(reportId);
        when(report.getPostId()).thenReturn(null);
        when(report.getCommentId()).thenReturn(commentId);
        when(reportRepository.findById(reportId)).thenReturn(Optional.of(report));
        
        // When
        ReportCommandResponse response = reportCommandService.processReport(reportId, ReportStatus.DELETED);
        
        // Then
        assertNotNull(response);
        verify(report, times(1)).updateStatus(ReportStatus.DELETED);
        verify(reportRepository, times(1)).findById(reportId);
        verify(commentService, times(1)).deleteCommentAsAdmin(commentId);
    }
} 