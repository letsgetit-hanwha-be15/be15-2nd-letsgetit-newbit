package com.newbit.newbitfeatureservice.coffeechat.command.application.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CoffeechatCancelRequest {
    @NotNull
    @Min(value = 1)
    private final Long coffeechatId;
    @NotNull
    @Min(value = 1)
    private final Long cancelReasonId;
}
