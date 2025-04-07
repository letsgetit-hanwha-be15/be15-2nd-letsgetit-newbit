package com.newbit.purchase.query.controller;

import com.newbit.common.dto.ApiResponse;
import com.newbit.purchase.query.dto.request.HistoryRequest;
import com.newbit.purchase.query.dto.response.ColumnPurchaseHistoryListResponse;
import com.newbit.purchase.query.service.ColumnPurchaseHistoryQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/purchase/column/history")
public class ColumnPurchaseHistoryQueryController {

    private final ColumnPurchaseHistoryQueryService columnPurchaseHistoryQueryService;


    //TODO: 스프링 시큐리티 + JWT 기반 인증 구조로 변경
    /*
    @GetMapping
    public ResponseEntity<ApiResponse<AssetHistoryListResponse>> getColumnPurchaseHistory(
            @ModelAttribute HistoryRequest requestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long userId = userDetails.getUserId(); // 로그인된 사용자 ID

        AssetHistoryListResponse response = columnPurchaseHistoryQueryService.getColumnPurchaseHistories(userId, requestDto);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
    */

    @GetMapping
    public ResponseEntity<ApiResponse<ColumnPurchaseHistoryListResponse>> getColumnPurchaseHistory(@ModelAttribute HistoryRequest requestDto) {
        ColumnPurchaseHistoryListResponse response = columnPurchaseHistoryQueryService.getColumnPurchaseHistories(requestDto);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}