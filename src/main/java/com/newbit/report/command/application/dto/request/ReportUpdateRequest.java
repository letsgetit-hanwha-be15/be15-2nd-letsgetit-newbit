package com.newbit.report.command.application.dto.request;

import com.newbit.report.command.domain.aggregate.ReportStatus;

import jakarta.validation.constraints.NotNull;

public record ReportUpdateRequest(
    @NotNull(message = "신고 ID는 필수입니다.") 
    Long reportId,
    
    @NotNull(message = "변경할 상태는 필수입니다.") 
    ReportStatus status
) {
}
