package com.newbit.newbitfeatureservice.subscription.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "시리즈 구독 상태 응답")
public class SubscriptionStatusResponse {
    
    @Schema(description = "시리즈 ID", example = "42")
    private Long seriesId;
    
    @Schema(description = "사용자 ID", example = "15")
    private Long userId;
    
    @Schema(description = "구독 여부", example = "true")
    private boolean isSubscribed;
    
    @Schema(description = "구독자 수", example = "325")
    private int totalSubscribers;
    
    public static SubscriptionStatusResponse of(Long seriesId, Long userId, boolean isSubscribed, int totalSubscribers) {
        return SubscriptionStatusResponse.builder()
                .seriesId(seriesId)
                .userId(userId)
                .isSubscribed(isSubscribed)
                .totalSubscribers(totalSubscribers)
                .build();
    }
} 