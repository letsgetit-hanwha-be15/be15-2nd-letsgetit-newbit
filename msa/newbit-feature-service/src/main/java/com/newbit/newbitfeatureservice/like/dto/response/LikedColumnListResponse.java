package com.newbit.newbitfeatureservice.like.dto.response;

import java.util.List;

import com.newbit.newbitfeatureservice.common.dto.Pagination;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "사용자가 좋아요한 칼럼 목록 응답")
public class LikedColumnListResponse {
    @Schema(description = "좋아요한 칼럼 목록")
    private List<LikedColumnResponse> likedColumns;
    
    @Schema(description = "페이지네이션 정보")
    private Pagination pagination;
} 