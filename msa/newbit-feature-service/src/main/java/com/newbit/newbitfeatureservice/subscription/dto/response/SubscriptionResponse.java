package com.newbit.newbitfeatureservice.subscription.dto.response;

import java.time.LocalDateTime;

import com.newbit.newbitfeatureservice.subscription.entity.Subscription;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "시리즈 구독 응답")
public class SubscriptionResponse {
    
    @Schema(description = "시리즈 ID", example = "42")
    private Long seriesId;
    
    @Schema(description = "사용자 ID", example = "15")
    private Long userId;
    
    @Schema(description = "구독 상태 (true: 구독중, false: 구독취소)", example = "true")
    private boolean subscribed;
    
    @Schema(description = "구독 시작 시간", example = "2023-08-15T14:30:15")
    private LocalDateTime createdAt;
    
    @Schema(description = "상태 변경 시간", example = "2023-08-15T14:30:15")
    private LocalDateTime updatedAt;
    
    public static SubscriptionResponse from(Subscription subscription) {
        return SubscriptionResponse.builder()
                .seriesId(subscription.getSeriesId())
                .userId(subscription.getUserId())
                .subscribed(true)
                .createdAt(subscription.getCreatedAt())
                .updatedAt(subscription.getUpdatedAt())
                .build();
    }
    
    public static SubscriptionResponse canceledSubscription(Long seriesId, Long userId) {
        return SubscriptionResponse.builder()
                .seriesId(seriesId)
                .userId(userId)
                .subscribed(false)
                .build();
    }
} 