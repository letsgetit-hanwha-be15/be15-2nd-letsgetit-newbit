package com.newbit.newbitfeatureservice.coffeechat.query.dto.request;

import lombok.Builder;

@Builder
public class CoffeechatProgressServiceRequest {
    private Long mentorId;
    private Long menteeId;
}
