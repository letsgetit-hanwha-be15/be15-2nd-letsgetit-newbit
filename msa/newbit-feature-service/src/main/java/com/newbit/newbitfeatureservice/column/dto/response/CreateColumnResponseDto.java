package com.newbit.newbitfeatureservice.column.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "멘토 칼럼 등록 요청 응답")
public class CreateColumnResponseDto {

    @Schema(description = "칼럼 요청 ID", example = "1")
    private Long columnRequestId;
}
