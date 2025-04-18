package com.newbit.newbitfeatureservice.column.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "관리자 칼럼 요청 응답")
public class AdminColumnResponseDto {

    @Schema(description = "칼럼 요청 ID", example = "1")
    Long requestId;

    @Schema(description = "요청 유형", example = "CREATE")
    String requestType;

    @Schema(description = "요청 승인 여부", example = "true")
    Boolean isApproved;

    @Schema(description = "칼럼 ID", example = "10")
    Long columnId;

}
