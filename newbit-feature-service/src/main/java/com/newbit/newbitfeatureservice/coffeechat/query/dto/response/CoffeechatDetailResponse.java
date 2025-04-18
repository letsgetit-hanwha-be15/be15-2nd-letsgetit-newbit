package com.newbit.newbitfeatureservice.coffeechat.query.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CoffeechatDetailResponse {
    private final CoffeechatDto coffeechat;
}
