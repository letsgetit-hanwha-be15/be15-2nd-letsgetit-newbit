package com.newbit.newbitfeatureservice.column.dto.response;

import com.newbit.newbitfeatureservice.column.enums.RequestType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "멘토 본인 칼럼 요청 목록 응답")
public class GetMyColumnRequestResponseDto {

    @Schema(description = "칼럼 요청 ID", example = "1")
    private Long columnRequestId;

    @Schema(description = "요청 타입", example = "CREATE / UPDATE / DELETE")
    private RequestType requestType;

    @Schema(description = "승인 여부", example = "false")
    private Boolean isApproved;

    @Schema(description = "요청한 제목", example = "나의 성장 이야기")
    private String title;

    @Schema(description = "요청한 가격", example = "1000")
    private Integer price;

    @Schema(description = "요청한 썸네일", example = "https://example.com/img.jpg")
    private String thumbnailUrl;

    @Schema(description = "요청 생성일", example = "2025-04-10T14:22:30")
    private LocalDateTime createdAt;
}
