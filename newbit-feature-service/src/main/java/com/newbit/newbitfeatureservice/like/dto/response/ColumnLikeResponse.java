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
@Schema(description = "칼럼 좋아요 응답")
public class ColumnLikeResponse {
    @Schema(description = "좋아요 ID", example = "1")
    private Long id;
    
    @Schema(description = "칼럼 ID", example = "42")
    private Long columnId;
    
    @Schema(description = "사용자 ID", example = "15")
    private Long userId;
    
    @Schema(description = "좋아요 생성 시간", example = "2023-08-15T14:30:15")
    private LocalDateTime createdAt;
    
    @Schema(description = "좋아요 상태 (true: 좋아요, false: 좋아요 취소)", example = "true")
    private boolean liked;
    
    @Schema(description = "칼럼 총 좋아요 수", example = "325")
    private int totalLikes;
    
    public static ColumnLikeResponse of(Like like, int totalLikes) {
        return ColumnLikeResponse.builder()
                .id(like.getId())
                .columnId(like.getColumnId())
                .userId(like.getUserId())
                .createdAt(like.getCreatedAt())
                .liked(true)
                .totalLikes(totalLikes)
                .build();
    }
    
    public static ColumnLikeResponse unliked(Long columnId, Long userId, int totalLikes) {
        return ColumnLikeResponse.builder()
                .columnId(columnId)
                .userId(userId)
                .liked(false)
                .totalLikes(totalLikes)
                .build();
    }
} 