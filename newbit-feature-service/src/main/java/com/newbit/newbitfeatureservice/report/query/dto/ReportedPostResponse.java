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

    @Builder
    public ReportedPostResponse(Long postId, String postTitle, Long reportCount, String lastReportContent) {
        this.postId = postId;
        this.postTitle = postTitle;
        this.reportCount = reportCount;
        this.lastReportContent = lastReportContent;
    }
} 