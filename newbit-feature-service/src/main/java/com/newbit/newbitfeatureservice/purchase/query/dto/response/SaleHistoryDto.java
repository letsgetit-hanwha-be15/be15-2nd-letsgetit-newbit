package com.newbit.newbitfeatureservice.purchase.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "멘토 정산 내역 DTO")
public class SaleHistoryDto {

    @Schema(description = "정산 내역 ID", example = "1")
    private Long settlementHistoryId;

    @Schema(description = "정산 여부", example = "false")
    private boolean isSettled;

    @Schema(description = "정산 완료 일시", example = "2025-04-10T14:30:00")
    private LocalDateTime settledAt;

    @Schema(description = "판매 금액", example = "500.00")
    private BigDecimal saleAmount;

    @Schema(description = "서비스 유형 (COFFEECHAT 또는 COLUMN)", example = "COFFEECHAT")
    private String serviceType;

    @Schema(description = "서비스 ID (칼럼 또는 커피챗 ID)", example = "10")
    private Long serviceId;

    @Schema(description = "생성 일시", example = "2025-04-10T13:15:00")
    private LocalDateTime createdAt;

    @Schema(description = "수정 일시", example = "2025-04-10T13:15:00")
    private LocalDateTime updatedAt;

    @Schema(description = "멘토 ID", example = "2")
    private Long mentorId;
}