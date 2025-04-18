package com.newbit.newbitfeatureservice.settlement.dto.response;

import com.newbit.newbitfeatureservice.settlement.entity.MonthlySettlementHistory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/* 하나의 정산 내역을 표현
 *  리스트 안에 들어가는 "요소" 역할
 *  클라이언트에게 각 정산 항목의 세부 정보(정산 금액, 연/월, 정산일시 등)를 제공 */
@Getter
@Builder
@Schema(description = "멘토 월별 정산 요약 DTO")
public class MentorSettlementSummaryDto {
    @Schema(description = "정산 ID", example = "1")
    private Long settlementId;

    @Schema(description = "정산 연도", example = "2025")
    private int settlementYear;

    @Schema(description = "정산 월", example = "4")
    private int settlementMonth;

    @Schema(description = "정산 금액", example = "120000.00")
    private BigDecimal settlementAmount;

    @Schema(description = "정산 완료 일시", example = "2025-04-01T14:30:00")
    private LocalDateTime settledAt;

    public static MentorSettlementSummaryDto from(MonthlySettlementHistory history) {
        return MentorSettlementSummaryDto.builder()
                .settlementId(history.getMonthlySettlementHistoryId())
                .settlementYear(history.getSettlementYear())
                .settlementMonth(history.getSettlementMonth())
                .settlementAmount(history.getSettlementAmount())
                .settledAt(history.getSettledAt())
                .build();
    }
}
