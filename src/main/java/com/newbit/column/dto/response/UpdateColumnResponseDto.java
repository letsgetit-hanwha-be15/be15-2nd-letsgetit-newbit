package com.newbit.column.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "칼럼 수정 요청 응답 DTO")
public class UpdateColumnResponseDto {

    @Schema(description = "생성된 수정 요청 ID", example = "5")
    private Long columnRequestId;
}
