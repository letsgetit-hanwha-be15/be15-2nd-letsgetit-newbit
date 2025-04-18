package com.newbit.newbitfeatureservice.column.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class RejectColumnRequestDto {

    @Schema(description = "거절할 칼럼 요청 ID", example = "1")
    private Long columnRequestId;

    @Schema(description = "거절 사유", example = "내용이 부적절합니다.")
    private String reason;
}
