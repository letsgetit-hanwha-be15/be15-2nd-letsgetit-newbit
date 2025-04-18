package com.newbit.newbitfeatureservice.report.command.application.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReportCreateRequest {

    @NotNull(message = "사용자 ID는 필수입니다.")
    private Long userId;

    private Long postId;

    private Long commentId;

    @NotNull(message = "신고 유형은 필수입니다.")
    private Long reportTypeId;

    @Size(max = 255, message = "신고 내용은 255자 이하로 작성해주세요.")
    private String content;

    public ReportCreateRequest(Long userId, Long postId, Long reportTypeId, String content) {
        this.userId = userId;
        this.postId = postId;
        this.reportTypeId = reportTypeId;
        this.content = content;
    }
    

    public ReportCreateRequest(Long userId, Long commentId, Long reportTypeId, String content, boolean isCommentReport) {
        this.userId = userId;
        this.commentId = commentId;
        this.reportTypeId = reportTypeId;
        this.content = content;
    }
}
