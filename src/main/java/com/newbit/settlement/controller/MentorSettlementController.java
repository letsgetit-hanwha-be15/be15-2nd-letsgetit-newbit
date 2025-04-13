package com.newbit.settlement.controller;

import com.newbit.settlement.service.MentorSettlementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/settlements")
@Tag(name = "멘토 정산", description = "멘토 월별 정산 관련 API")
public class MentorSettlementController {

    private final MentorSettlementService settlementService;

    @PostMapping("/generate")
    @Operation(
            summary = "멘토 월별 정산 생성",
            description = "특정 연도와 월을 기준으로 멘토 정산 내역을 생성하고, 관련된 판매내역을 정산 완료 상태로 변경합니다."
    )
    public ResponseEntity<Void> generateMonthlySettlement(
            @RequestParam int year,
            @RequestParam int month
    ) {
        settlementService.generateMonthlySettlements(year, month);
        return ResponseEntity.ok().build();
    }
}

