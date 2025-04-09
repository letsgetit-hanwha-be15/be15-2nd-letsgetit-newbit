package com.newbit.report.command.domain.aggregate;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

    @Column(nullable = false)
    private Long reportTypeId;

    @Column(length = 255)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Builder
    public Report(Long userId, Long postId, Long reportTypeId, String content) {
        this.userId = userId;
        this.postId = postId;
        this.reportTypeId = reportTypeId;
        this.content = content;
        this.status = ReportStatus.SUBMITTED;
        this.createdAt = LocalDateTime.now();
    }

    public void updateStatus(ReportStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public static Report createPostReport(Long userId, Long postId, Long reportTypeId, String content) {
        return Report.builder()
                .userId(userId)
                .postId(postId)
                .reportTypeId(reportTypeId)
                .content(content)
                .build();
    }
}
