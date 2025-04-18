package com.newbit.newbitfeatureservice.coffeechat.query.dto.response;

import com.newbit.newbitfeatureservice.common.dto.Pagination;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ReviewListResponse {
    @Schema(description = "리뷰 리스트")
    private final List<ReviewListDto> reviews;
    @Schema(description = "페이지네이션 정보")
    private final Pagination pagination;
}
