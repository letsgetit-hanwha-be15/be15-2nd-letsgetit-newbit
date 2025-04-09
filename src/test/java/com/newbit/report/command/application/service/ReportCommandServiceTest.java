package com.newbit.report.command.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.newbit.report.command.application.dto.request.ReportCreateRequest;
import com.newbit.report.command.application.dto.response.ReportCommandResponse;
import com.newbit.report.command.domain.aggregate.Report;
import com.newbit.report.command.domain.aggregate.ReportStatus;
import com.newbit.report.command.domain.repository.ReportRepository;

@ExtendWith(MockitoExtension.class)
class ReportCommandServiceTest {

    @Mock
    private ReportRepository reportRepository;

    @InjectMocks
    private ReportCommandService reportCommandService;

    @Test
    @DisplayName("게시글 신고 생성 서비스 테스트")
    void createPostReportTest() {
        // Given
        Long userId = 1L;
        Long postId = 2L;
        Long reportTypeId = 1L;
        String content = "이 게시글은 스팸입니다.";

        ReportCreateRequest request = new ReportCreateRequest(userId, postId, reportTypeId, content);
        
        Report mockReport = Report.createPostReport(userId, postId, reportTypeId, content);

        when(reportRepository.save(any(Report.class))).thenReturn(mockReport);

        // When
        ReportCommandResponse response = reportCommandService.createPostReport(request);

        // Then
        // 1. 응답 값 검증
        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo(userId);
        assertThat(response.getPostId()).isEqualTo(postId);
        assertThat(response.getReportTypeId()).isEqualTo(reportTypeId);
        assertThat(response.getContent()).isEqualTo(content);
        assertThat(response.getStatus()).isEqualTo(ReportStatus.SUBMITTED);
        
        // 2. 저장 메서드 호출 여부 확인
        ArgumentCaptor<Report> reportCaptor = ArgumentCaptor.forClass(Report.class);
        verify(reportRepository).save(reportCaptor.capture());
        
        // 3. 저장된 엔티티 값 검증
        Report savedReport = reportCaptor.getValue();
        assertThat(savedReport.getUserId()).isEqualTo(userId);
        assertThat(savedReport.getPostId()).isEqualTo(postId);
        assertThat(savedReport.getReportTypeId()).isEqualTo(reportTypeId);
        assertThat(savedReport.getContent()).isEqualTo(content);
        assertThat(savedReport.getStatus()).isEqualTo(ReportStatus.SUBMITTED);
    }
    
    @Test
    @DisplayName("게시글 신고 생성 시 postId가 null이면 예외 발생")
    void createPostReportWithNullPostIdTest() {
        // Given
        Long userId = 1L;
        Long postId = null; // null postId
        Long reportTypeId = 1L;
        String content = "이 게시글은 스팸입니다.";

        ReportCreateRequest request = new ReportCreateRequest(userId, postId, reportTypeId, content);
        
        // When & Then
        assertThatThrownBy(() -> reportCommandService.createPostReport(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("게시글 ID는 필수입니다.");
    }
    
    @Test
    @DisplayName("댓글 신고 생성 서비스 테스트")
    void createCommentReportTest() {
        // Given
        Long userId = 1L;
        Long commentId = 3L;
        Long reportTypeId = 1L;
        String content = "이 댓글은 스팸입니다.";
        
        // 댓글 신고를 위한 생성자 사용
        ReportCreateRequest request = new ReportCreateRequest(userId, commentId, reportTypeId, content, true);
        
        Report mockReport = Report.createCommentReport(userId, commentId, reportTypeId, content);

        when(reportRepository.save(any(Report.class))).thenReturn(mockReport);

        // When
        ReportCommandResponse response = reportCommandService.createCommentReport(request);

        // Then
        // 1. 응답 값 검증
        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo(userId);
        assertThat(response.getCommentId()).isEqualTo(commentId);
        assertThat(response.getReportTypeId()).isEqualTo(reportTypeId);
        assertThat(response.getContent()).isEqualTo(content);
        assertThat(response.getStatus()).isEqualTo(ReportStatus.SUBMITTED);
        
        // 2. 저장 메서드 호출 여부 확인
        ArgumentCaptor<Report> reportCaptor = ArgumentCaptor.forClass(Report.class);
        verify(reportRepository).save(reportCaptor.capture());
        
        // 3. 저장된 엔티티 값 검증
        Report savedReport = reportCaptor.getValue();
        assertThat(savedReport.getUserId()).isEqualTo(userId);
        assertThat(savedReport.getCommentId()).isEqualTo(commentId);
        assertThat(savedReport.getReportTypeId()).isEqualTo(reportTypeId);
        assertThat(savedReport.getContent()).isEqualTo(content);
        assertThat(savedReport.getStatus()).isEqualTo(ReportStatus.SUBMITTED);
    }
    
    @Test
    @DisplayName("댓글 신고 생성 시 commentId가 null이면 예외 발생")
    void createCommentReportWithNullCommentIdTest() {
        // Given
        Long userId = 1L;
        Long commentId = null; // null commentId
        Long reportTypeId = 1L;
        String content = "이 댓글은 스팸입니다.";
        
        // commentId를 null로 설정하여 생성자 호출
        ReportCreateRequest request = new ReportCreateRequest(userId, commentId, reportTypeId, content, true);
        
        // When & Then
        assertThatThrownBy(() -> reportCommandService.createCommentReport(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("댓글 ID는 필수입니다.");
    }
} 