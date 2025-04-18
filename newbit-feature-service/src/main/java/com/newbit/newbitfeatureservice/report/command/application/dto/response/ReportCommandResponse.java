package com.newbit.newbitfeatureservice.report.command.application.dto.response;

import java.time.LocalDateTime;

import com.newbit.newbitfeatureservice.report.command.domain.aggregate.Report;
import com.newbit.newbitfeatureservice.report.command.domain.aggregate.ReportStatus;
import com.newbit.newbitfeatureservice.report.command.domain.aggregate.ReportType;

import lombok.Getter;

@Getter
public class ReportCommandResponse {
    private final Long reportId;
    private final Long userId;
    private final Long postId;
    private final Long commentId;
    private final ReportType reportType;
    private final String content;
    private final ReportStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public ReportCommandResponse(Report report) {
        this.reportId = report.getReportId();
        this.userId = report.getUserId();
        this.postId = report.getPostId();
        this.commentId = report.getCommentId();
        this.reportType = report.getReportType();
        this.content = report.getContent();
        this.status = report.getStatus();
        this.createdAt = report.getCreatedAt();
        this.updatedAt = report.getUpdatedAt();
    }
    
    public Long getReportTypeId() {
        return reportType != null ? reportType.getId() : null;
    }
}
