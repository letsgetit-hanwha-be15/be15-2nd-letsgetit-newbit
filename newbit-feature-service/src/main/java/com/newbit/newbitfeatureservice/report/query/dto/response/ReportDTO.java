package com.newbit.newbitfeatureservice.report.query.dto.response;

import java.time.LocalDateTime;

import com.newbit.newbitfeatureservice.report.command.domain.aggregate.ReportStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReportDTO {

    private Long reportId;
    private Long userId;
    private Long commentId;
    private Long postId;
    private ReportTypeDTO reportType;
    private String content;
    private ReportStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getPostId() { return postId; }
    public Long getCommentId() { return commentId; }
    public java.time.LocalDateTime getCreatedAt() { return createdAt; }
    public String getContent() { return content; }
    public com.newbit.newbitfeatureservice.report.command.domain.aggregate.ReportStatus getStatus() { return status; }
} 