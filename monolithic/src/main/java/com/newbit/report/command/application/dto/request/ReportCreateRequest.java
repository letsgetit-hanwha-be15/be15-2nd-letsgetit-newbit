package com.newbit.report.command.application.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReportCreateRequest(
    @NotNull(message = "사용자 ID는 필수입니다.")
    Long userId,
    
    Long postId,
    
    Long commentId,
    
    @NotNull(message = "신고 유형은 필수입니다.")
    Long reportTypeId,
    
    @Size(max = 255, message = "신고 내용은 255자 이하로 작성해주세요.")
    String content
) {

    public static ReportCreateRequest forPost(Long userId, Long postId, Long reportTypeId, String content) {
        return new ReportCreateRequest(userId, postId, null, reportTypeId, content);
    }

    public static ReportCreateRequest forComment(Long userId, Long commentId, Long reportTypeId, String content) {
        return new ReportCreateRequest(userId, null, commentId, reportTypeId, content);
    }
}
