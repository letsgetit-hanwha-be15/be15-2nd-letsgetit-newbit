package com.newbit.newbitfeatureservice.report.command.domain.aggregate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class ReportTest {
    
    private ReportType mockReportType;
    
    @BeforeEach
    void setUp() {
        // ReportType 모킹
        mockReportType = Mockito.mock(ReportType.class);
        when(mockReportType.getId()).thenReturn(1L);
        when(mockReportType.getName()).thenReturn("SPAM");
    }

    @Test
    @DisplayName("게시글 신고 생성 테스트")
    void createPostReportTest() {
        // Given
        Long userId = 1L;
        Long postId = 2L;
        String content = "이 게시글은 스팸입니다.";

        // When
        Report report = Report.createPostReport(userId, postId, mockReportType, content);

        // Then
        assertThat(report).isNotNull();
        assertThat(report.getUserId()).isEqualTo(userId);
        assertThat(report.getPostId()).isEqualTo(postId);
        assertThat(report.getCommentId()).isNull(); // 게시글 신고이므로 commentId는 null
        assertThat(report.getReportType()).isEqualTo(mockReportType);
        assertThat(report.getReportTypeId()).isEqualTo(1L);
        assertThat(report.getContent()).isEqualTo(content);
        assertThat(report.getStatus()).isEqualTo(ReportStatus.SUBMITTED);
        assertThat(report.getCreatedAt()).isNotNull();
        assertThat(report.getCreatedAt()).isBefore(LocalDateTime.now().plusSeconds(1));
        assertThat(report.getUpdatedAt()).isNull();
    }
    
    @Test
    @DisplayName("댓글 신고 생성 테스트")
    void createCommentReportTest() {
        // Given
        Long userId = 1L;
        Long commentId = 3L;
        String content = "이 댓글은 스팸입니다.";

        // When
        Report report = Report.createCommentReport(userId, commentId, mockReportType, content);

        // Then
        assertThat(report).isNotNull();
        assertThat(report.getUserId()).isEqualTo(userId);
        assertThat(report.getPostId()).isNull(); // 댓글 신고이므로 postId는 null
        assertThat(report.getCommentId()).isEqualTo(commentId);
        assertThat(report.getReportType()).isEqualTo(mockReportType);
        assertThat(report.getReportTypeId()).isEqualTo(1L);
        assertThat(report.getContent()).isEqualTo(content);
        assertThat(report.getStatus()).isEqualTo(ReportStatus.SUBMITTED);
        assertThat(report.getCreatedAt()).isNotNull();
        assertThat(report.getCreatedAt()).isBefore(LocalDateTime.now().plusSeconds(1));
        assertThat(report.getUpdatedAt()).isNull();
    }

    @Test
    @DisplayName("신고 상태 업데이트 테스트")
    void updateStatusTest() {
        // Given
        Report report = Report.createPostReport(1L, 2L, mockReportType, "스팸 신고");
        ReportStatus newStatus = ReportStatus.SUSPENDED;

        // When
        report.updateStatus(newStatus);

        // Then
        assertThat(report.getStatus()).isEqualTo(newStatus);
        assertThat(report.getUpdatedAt()).isNotNull();
        assertThat(report.getUpdatedAt()).isBefore(LocalDateTime.now().plusSeconds(1));
    }
} 