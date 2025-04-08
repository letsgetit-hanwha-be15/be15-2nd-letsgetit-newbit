package com.newbit.purchase.query.controller;

import com.newbit.common.dto.ApiResponse;
import com.newbit.purchase.query.dto.request.HistoryRequest;
import com.newbit.purchase.query.dto.response.ColumnPurchaseHistoryListResponse;
import com.newbit.purchase.query.service.ColumnPurchaseHistoryQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/purchase/column/history")
public class ColumnPurchaseHistoryQueryController {

    private final ColumnPurchaseHistoryQueryService columnPurchaseHistoryQueryService;

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<ColumnPurchaseHistoryListResponse>> getColumnPurchaseHistory(@PathVariable Long userId,  @ModelAttribute HistoryRequest requestDto) {
        requestDto.setUserId(userId);
        ColumnPurchaseHistoryListResponse response = columnPurchaseHistoryQueryService.getColumnPurchaseHistories(requestDto);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}