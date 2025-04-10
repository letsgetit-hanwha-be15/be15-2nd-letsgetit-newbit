package com.newbit.coffeechat.query.dto.request;

import com.newbit.coffeechat.command.domain.aggregate.ProgressStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CoffeechatSearchRequest {
    private Long userId;
}