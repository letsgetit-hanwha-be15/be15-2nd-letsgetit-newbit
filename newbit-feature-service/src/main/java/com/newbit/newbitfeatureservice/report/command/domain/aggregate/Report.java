package com.newbit.newbitfeatureservice.report.command.domain.aggregate;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "report")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;

    @Column(nullable = false)
    private Long userId;

    private Long commentId;

    private Long postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_type_id", nullable = false)
    private ReportType reportType;

    @Column(length = 255)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Builder
    private Report(Long userId, Long postId, Long commentId, ReportType reportType, String content) {
        this.userId = userId;
        this.postId = postId;
        this.commentId = commentId;
        this.reportType = reportType;
        this.content = content;
        this.status = ReportStatus.SUBMITTED;
        this.createdAt = LocalDateTime.now();
    }

    public void updateStatus(ReportStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public static Report createPostReport(Long userId, Long postId, ReportType reportType, String content) {
        return Report.builder()
                .userId(userId)
                .postId(postId)
                .reportType(reportType)
                .content(content)
                .build();
    }
    
    public static Report createCommentReport(Long userId, Long commentId, ReportType reportType, String content) {
        return Report.builder()
                .userId(userId)
                .commentId(commentId)
                .reportType(reportType)
                .content(content)
                .build();
    }
    
    /**
     * 기존 테스트 코드와의 호환성을 위해 ReportType의 ID를 반환
     * @return 신고 유형 ID
     */
    public Long getReportTypeId() {
        return reportType != null ? reportType.getId() : null;
    }
}
