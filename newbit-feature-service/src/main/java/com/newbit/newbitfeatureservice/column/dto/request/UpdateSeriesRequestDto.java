package com.newbit.newbitfeatureservice.column.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class UpdateSeriesRequestDto {

    @Schema(description = "수정할 시리즈 목록", example = "이직 준비 리뉴얼")
    @NotBlank
    private String title;

    @Schema(description = "수정할 시리즈 설명", example = "리뉴얼 이직 시리즈")
    @NotBlank
    private String description;

    @Schema(description = "수정할 대표 이미지 URL", example = "https://example.com/new-thumb.jpg")
    private String thumbnailUrl;

    @Schema(description = "포함할 칼럼 ID 목록", example = "[1, 2, 3]")
    @NotEmpty(message = "최소 하나 이상의 칼럼 ID가 필요합니다.")
    private List<Long> columnIds;
}
