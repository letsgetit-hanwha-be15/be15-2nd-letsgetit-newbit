package com.newbit.newbitfeatureservice.settlement.dto.response;

import com.newbit.newbitfeatureservice.settlement.entity.MonthlySettlementHistory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "멘토 정산 상세 내역 응답 DTO")
public class MentorSettlementDetailResponseDto {

    @Schema(description = "정산 ID", example = "1")
    private Long settlementId;

    @Schema(description = "정산 연도", example = "2025")
    private int year;

    @Schema(description = "정산 월", example = "4")
    private int month;

    @Schema(description = "정산 금액", example = "150000.00")
    private BigDecimal amount;

    @Schema(description = "정산 완료 시각", example = "2025-04-30T23:59:59")
    private LocalDateTime settledAt;

    public static MentorSettlementDetailResponseDto from(MonthlySettlementHistory history) {
        return MentorSettlementDetailResponseDto.builder()
                .settlementId(history.getMonthlySettlementHistoryId())
                .year(history.getSettlementYear())
                .month(history.getSettlementMonth())
                .amount(history.getSettlementAmount())
                .settledAt(history.getSettledAt())
                .build();
    }
}
