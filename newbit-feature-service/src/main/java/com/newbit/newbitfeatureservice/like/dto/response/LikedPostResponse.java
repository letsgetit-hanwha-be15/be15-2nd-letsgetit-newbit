package com.newbit.newbitfeatureservice.like.dto.response;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "사용자가 좋아요한 게시글 정보")
public class LikedPostResponse {
    @Schema(description = "좋아요 ID", example = "1")
    private Long likeId;
    
    @Schema(description = "게시글 ID", example = "42")
    private Long postId;
    
    @Schema(description = "게시글 제목", example = "흥미로운 개발 이야기")
    private String postTitle;
    
    @Schema(description = "작성자 ID", example = "15")
    private Long authorId;
    
    @Schema(description = "좋아요 생성 시간", example = "2023-08-15T14:30:15")
    private LocalDateTime likedAt;
} 