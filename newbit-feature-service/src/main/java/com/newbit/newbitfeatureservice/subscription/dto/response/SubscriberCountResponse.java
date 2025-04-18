package com.newbit.newbitfeatureservice.subscription.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "구독자 수 응답")
public class SubscriberCountResponse {

    @Schema(description = "시리즈 ID", example = "1")
    private Long seriesId;
    
    @Schema(description = "구독자 수", example = "325")
    private int subscriberCount;
} 