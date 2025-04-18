package com.newbit.newbitfeatureservice.coffeechat.command.application.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
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
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Size(max = 3)
    private final List<@Future LocalDateTime> requestTimes; // 요청 시간 : 시작 시간 기준, 끝 시간은 내부적으로 계산
}
