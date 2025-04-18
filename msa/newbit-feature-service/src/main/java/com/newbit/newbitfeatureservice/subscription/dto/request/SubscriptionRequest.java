package com.newbit.newbitfeatureservice.subscription.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "시리즈 구독 요청")
public class SubscriptionRequest {
    
    @NotNull(message = "시리즈 ID는 필수입니다.")
    @Schema(description = "시리즈 ID", example = "42", required = true)
    private Long seriesId;
} 