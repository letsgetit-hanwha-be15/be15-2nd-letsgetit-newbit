package com.newbit.newbitfeatureservice.column.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class ApproveColumnRequestDto {

    @Schema(description = "승인할 칼럼 요청 ID", example = "1")
    private Long columnRequestId;
}
