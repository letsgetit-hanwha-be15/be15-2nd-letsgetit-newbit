package com.newbit.newbitfeatureservice.purchase.command.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CoffeeChatPurchaseRequest {
    @NotNull(message = "coffeechatId는 필수입니다.")
    @Schema(description = "커피챗 ID", example = "1")
    private Long coffeechatId;
}
