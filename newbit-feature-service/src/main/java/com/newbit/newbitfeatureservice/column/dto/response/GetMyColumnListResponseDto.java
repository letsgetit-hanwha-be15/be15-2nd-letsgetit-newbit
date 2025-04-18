package com.newbit.newbitfeatureservice.column.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "멘토 본인 승인된 칼럼 목록 응답")
public class GetMyColumnListResponseDto {

    @Schema(description = "칼럼 ID", example = "1")
    private Long columnId;

    @Schema(description = "제목", example = "나의 개발 이야기")
    private String title;

    @Schema(description = "썸네일 URL", example = "https://example.com/image.jpg")
    private String thumbnailUrl;

    @Schema(description = "가격", example = "1000")
    private Integer price;

    @Schema(description = "좋아요 수", example = "23")
    private Integer likeCount;
}
