package com.newbit.column.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetColumnListResponseDto {

    @Schema(description = "칼럼 ID", example = "1")
    private Long columnId;

    @Schema(description = "제목", example = "이직을 위한 포트폴리오 전략")
    private String title;

    @Schema(description = "썸네일 URL", example = "https://example.com/image.jpg")
    private String thumbnailUrl;

    @Schema(description = "가격", example = "1000")
    private Integer price;

    @Schema(description = "좋아요 수", example = "12")
    private Integer likeCount;

    @Schema(description = "멘토 ID", example = "5")
    private Long mentorId;
}
