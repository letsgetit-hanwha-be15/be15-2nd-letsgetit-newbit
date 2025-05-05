package com.newbit.newbitfeatureservice.report.query.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReportedPostResponse {
    private Long postId;
    private String postTitle;
    private Long reportCount;
    private String lastReportContent;
    private String lastReportDate;
    private String lastReportStatus;

    @Builder
    public ReportedPostResponse(Long postId, String postTitle, Long reportCount, String lastReportContent, String lastReportDate, String lastReportStatus) {
        this.postId = postId;
        this.postTitle = postTitle;
        this.reportCount = reportCount;
        this.lastReportContent = lastReportContent;
        this.lastReportDate = lastReportDate;
        this.lastReportStatus = lastReportStatus;
    }

    public Long getReportCount() { return reportCount; }
    public String getLastReportDate() { return lastReportDate; }
} 