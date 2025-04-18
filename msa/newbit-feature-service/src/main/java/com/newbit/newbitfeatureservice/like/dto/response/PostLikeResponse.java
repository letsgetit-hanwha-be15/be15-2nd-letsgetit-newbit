package com.newbit.newbitfeatureservice.like.dto.response;

import java.time.LocalDateTime;

import com.newbit.newbitfeatureservice.like.entity.Like;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "게시글 좋아요 응답")
public class PostLikeResponse {
    @Schema(description = "좋아요 ID", example = "1")
    private Long id;
    
    @Schema(description = "게시글 ID", example = "42")
    private Long postId;
    
    @Schema(description = "사용자 ID", example = "15")
    private Long userId;
    
    @Schema(description = "좋아요 생성 시간", example = "2023-08-15T14:30:15")
    private LocalDateTime createdAt;
    
    @Schema(description = "좋아요 상태 (true: 좋아요, false: 좋아요 취소)", example = "true")
    private boolean liked;
    
    @Schema(description = "게시글 총 좋아요 수", example = "325")
    private int totalLikes;
    
    public static PostLikeResponse of(Like like, int totalLikes) {
        return PostLikeResponse.builder()
                .id(like.getId())
                .postId(like.getPostId())
                .userId(like.getUserId())
                .createdAt(like.getCreatedAt())
                .liked(true)
                .totalLikes(totalLikes)
                .build();
    }
    
    public static PostLikeResponse unliked(Long postId, Long userId, int totalLikes) {
        return PostLikeResponse.builder()
                .postId(postId)
                .userId(userId)
                .liked(false)
                .totalLikes(totalLikes)
                .build();
    }
} 