package com.newbit.newbitfeatureservice.column.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CreateSeriesRequestDto {

    @Schema(description = "시리즈 제목", example = "이직 준비 A to Z")
    @NotBlank
    private String title;

    @Schema(description = "시리즈 설명", example = "이직을 준비하는 멘티들을 위한 시리즈입니다.")
    @NotBlank
    private String description;

    @Schema(description = "대표 이미지 URL", example = "https://example.com/img.jpg")
    private String thumbnailUrl;

    @Schema(description = "시리즈에 포함될 칼럼 ID 목록", example = "[1, 2, 3]")
    @NotEmpty(message = "최소 하나 이상의 칼럼 ID가 필요합니다.")
    private List<Long> columnIds;
}

