package com.newbit.newbitfeatureservice.column.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateColumnRequestDto {

    @Schema(description = "칼럼 제목", example = "나의 성장 이야기")
    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    @Schema(description = "시리즈 ID", example = "1")
    private Long seriesId;

    @Schema(description = "칼럼 본문 내용", example = "저는 이렇게 성장했습니다...")
    @NotBlank(message = "내용은 필수입니다.")
    private String content;

    @Schema(description = "칼럼 가격", example = "1000")
    @NotNull(message = "가격은 필수입니다.")
    private Integer price;

    @Schema(description = "썸네일 이미지 URL", example = "https://example.com/img.png")
    private String thumbnailUrl;
}
