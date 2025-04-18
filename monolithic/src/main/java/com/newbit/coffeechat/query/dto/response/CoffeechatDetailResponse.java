package com.newbit.coffeechat.query.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class CoffeechatDetailResponse {
    private final CoffeechatDto coffeechat;
}
