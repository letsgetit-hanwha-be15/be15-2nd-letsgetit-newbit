package com.newbit.coffeechat.command.application.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

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
    @Setter
    private Long menteeId;
    @NotNull
    @Size(max = 3)
    private List<RequestTimeDto> requestTimes;
}
