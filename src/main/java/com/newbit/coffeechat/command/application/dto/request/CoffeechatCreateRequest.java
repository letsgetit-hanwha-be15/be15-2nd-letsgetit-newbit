package com.newbit.coffeechat.command.application.dto.request;

import com.newbit.coffeechat.command.domain.aggregate.ProgressStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CoffeechatCreateRequest {
    @NotNull
    @NotBlank
    private final String requestMessage;
    @NotNull
    @Min(value = 1)
    private final int purchaseQuantity;
    @NotNull
    @Min(value = 1)
    private final Long mentorId;
}
