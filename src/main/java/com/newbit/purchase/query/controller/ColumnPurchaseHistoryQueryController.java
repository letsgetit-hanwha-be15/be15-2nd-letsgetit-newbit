package com.newbit.purchase.query.controller;

import com.newbit.common.dto.ApiResponse;
import com.newbit.purchase.query.dto.request.HistoryRequest;
import com.newbit.purchase.query.dto.response.ColumnPurchaseHistoryListResponse;
import com.newbit.purchase.query.service.ColumnPurchaseHistoryQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "구매관련 API", description = "칼럼 구매 이력 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/purchase/column/history")
public class ColumnPurchaseHistoryQueryController {

    private final ColumnPurchaseHistoryQueryService columnPurchaseHistoryQueryService;

    @Operation(summary = "칼럼 구매 내역 조회", description = "특정 유저의 칼럼 구매 내역을 조회합니다.")
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<ColumnPurchaseHistoryListResponse>> getColumnPurchaseHistory(
            @Parameter(description = "조회할 유저 ID", required = true) @PathVariable Long userId,
            @ModelAttribute HistoryRequest requestDto) {
        requestDto.setUserId(userId);
        ColumnPurchaseHistoryListResponse response = columnPurchaseHistoryQueryService.getColumnPurchaseHistories(requestDto);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}