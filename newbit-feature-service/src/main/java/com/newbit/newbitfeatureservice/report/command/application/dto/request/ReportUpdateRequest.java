package com.newbit.newbitfeatureservice.report.command.application.dto.request;

import com.newbit.newbitfeatureservice.report.command.domain.aggregate.ReportStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReportUpdateRequest {

    @NotNull(message = "신고 ID는 필수입니다.")
    private Long reportId;
    
    @NotNull(message = "변경할 상태는 필수입니다.")
    private ReportStatus status;
    
    public ReportUpdateRequest(Long reportId, ReportStatus status) {
        this.reportId = reportId;
        this.status = status;
    }
}
