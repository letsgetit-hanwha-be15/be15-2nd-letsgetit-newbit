package com.newbit.purchase.query.controller;


import com.newbit.common.dto.ApiResponse;
import com.newbit.purchase.query.dto.request.HistoryRequest;
import com.newbit.purchase.query.dto.response.ColumnPurchaseHistoryListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "구매관련 API", description = "멘토 판매 내역 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/purchase/sale/history")
public class SaleHistoryQueryController {

    private final SaleHistoryQueryService saleHistoryQueryService;

    @Operation(summary = "판매 내역 조회", description = "특정 멘토의 판매 내역을 조회합니다.")
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<SaleHistoryListResponse>> getSaleHistory(
            @Parameter(description = "조회할 유저 ID", required = true) @PathVariable Long userId,
            @ModelAttribute HistoryRequest requestDto) {
        requestDto.setUserId(userId);
        ColumnPurchaseHistoryListResponse response = saleHistoryQueryService.getSaleHistories(requestDto);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}