package com.newbit.column.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "시리즈에 포함된 칼럼 목록 응답")
public class GetSeriesColumnsResponseDto {

    @Schema(description = "칼럼 ID", example = "1")
    private Long columnId;

    @Schema(description = "칼럼 제목", example = "포트폴리오 작성법")
    private String title;

    @Schema(description = "썸네일 URL", example = "https://example.com/thumb.jpg")
    private String thumbnailUrl;

    @Schema(description = "가격", example = "3000")
    private Integer price;

    @Schema(description = "좋아요 수", example = "12")
    private Integer likeCount;
}
