package com.newbit.newbitfeatureservice.column.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateColumnRequestDto {
    @Schema(description = "수정할 칼럼 제목", example = "수정된 칼럼 제목")
    private String title;

    @Schema(description = "수정할 칼럼 내용", example = "수정된 내용입니다.")
    private String content;

    @Schema(description = "수정할 칼럼 가격", example = "2000")
    private Integer price;

    @Schema(description = "수정할 썸네일 이미지 URL", example = "https://example.com/image.jpg")
    private String thumbnailUrl;
}