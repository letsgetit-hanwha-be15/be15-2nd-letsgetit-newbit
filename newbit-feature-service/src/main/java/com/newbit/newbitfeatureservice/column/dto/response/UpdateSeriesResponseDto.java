package com.newbit.newbitfeatureservice.column.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "시리즈 수정 응답")
public class UpdateSeriesResponseDto {

    @Schema(description = "시리즈 ID", example = "1")
    private Long seriesId;

    @Schema(description = "시리즈 제목", example = "이직 준비 A to Z")
    private String title;

    @Schema(description = "시리즈 설명", example = "이직을 준비하는 멘티들을 위한 시리즈입니다.")
    private String description;

    @Schema(description = "대표 이미지 URL", example = "https://example.com/image.jpg")
    private String thumbnailUrl;
}
