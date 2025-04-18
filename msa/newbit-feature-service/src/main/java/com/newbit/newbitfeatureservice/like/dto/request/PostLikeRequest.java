package com.newbit.newbitfeatureservice.like.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "게시글 좋아요 요청")
public class PostLikeRequest {
    @Schema(description = "게시글 ID (필수)", example = "1")
    private Long postId;
} 