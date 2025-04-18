package com.newbit.newbitfeatureservice.column.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "멘토 칼럼 수정 요청 응답")
public class UpdateColumnResponseDto {

    @Schema(description = "칼럼 요청 ID", example = "5")
    private Long columnRequestId;
}
