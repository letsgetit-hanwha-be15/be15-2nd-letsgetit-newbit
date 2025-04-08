package com.newbit.column.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeleteColumnRequestDto {

    @Schema(description = "삭제 요청 사유", example = "더 이상 제공하지 않는 칼럼입니다.")
    private String reason; // 삭제 요청 사유
}
