package com.newbit.report.command.application.dto.response;

import java.time.LocalDateTime;

import com.newbit.report.command.domain.aggregate.Report;
import com.newbit.report.command.domain.aggregate.ReportStatus;

import lombok.Getter;

@Getter
public class ReportCommandResponse {
    private final Long reportId;
    private final Long userId;
    private final Long postId;
    private final Long commentId;
    private final Long reportTypeId;
    private final String content;
    private final ReportStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public ReportCommandResponse(Report report) {
        this.reportId = report.getReportId();
        this.userId = report.getUserId();
        this.postId = report.getPostId();
        this.commentId = report.getCommentId();
        this.reportTypeId = report.getReportTypeId();
        this.content = report.getContent();
        this.status = report.getStatus();
        this.createdAt = report.getCreatedAt();
        this.updatedAt = report.getUpdatedAt();
    }
}
