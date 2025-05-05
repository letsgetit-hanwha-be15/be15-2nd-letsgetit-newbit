package com.newbit.newbitfeatureservice.report.query.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReportedCommentResponse {
    private Long commentId;
    private String commentContent; // Assuming comments have content, not title
    private Long reportCount;
    private String lastReportContent;
    private String lastReportDate;
    private String lastReportStatus;

    @Builder
    public ReportedCommentResponse(Long commentId, String commentContent, Long reportCount, String lastReportContent, String lastReportDate, String lastReportStatus) {
        this.commentId = commentId;
        this.commentContent = commentContent;
        this.reportCount = reportCount;
        this.lastReportContent = lastReportContent;
        this.lastReportDate = lastReportDate;
        this.lastReportStatus = lastReportStatus;
    }

    public Long getReportCount() { return reportCount; }
    public String getLastReportDate() { return lastReportDate; }
} 