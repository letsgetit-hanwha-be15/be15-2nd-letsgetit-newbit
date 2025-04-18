package com.newbit.newbitfeatureservice.purchase.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssetHistoryDto {

    @Schema(description = "히스토리 ID", example = "1")
    private Long historyId;

    @Schema(description = "서비스 유형")
    private String serviceType;

    @Schema(description = "서비스 ID (ex. columnId, coffeechatId 등)", example = "1")
    private Long serviceId;

    @Schema(description = "증가한 금액", example = "1000")
    private Integer increaseAmount;

    @Schema(description = "감소한 금액", example = "500")
    private Integer decreaseAmount;

    @Schema(description = "해당 시점 잔액", example = "9500")
    private int balance;

    @Schema(description = "생성 일시", example = "2025-04-08T14:33:00")
    private LocalDateTime createdAt;
}
