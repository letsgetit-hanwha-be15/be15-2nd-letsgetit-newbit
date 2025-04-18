package com.newbit.newbitfeatureservice.column.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "시리즈 생성 응답 DTO")
public class CreateSeriesResponseDto {

    @Schema(description = "생성된 시리즈 ID", example = "1")
    private Long seriesId;
}

