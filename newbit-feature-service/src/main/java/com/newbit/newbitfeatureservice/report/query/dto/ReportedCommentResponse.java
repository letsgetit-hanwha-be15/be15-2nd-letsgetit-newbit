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

    @Builder
    public ReportedCommentResponse(Long commentId, String commentContent, Long reportCount, String lastReportContent) {
        this.commentId = commentId;
        this.commentContent = commentContent;
        this.reportCount = reportCount;
        this.lastReportContent = lastReportContent;
    }
} 