package com.newbit.newbitfeatureservice.purchase.query.controller;

import com.newbit.newbitfeatureservice.common.dto.ApiResponse;
import com.newbit.newbitfeatureservice.purchase.query.dto.request.HistoryRequest;
import com.newbit.newbitfeatureservice.purchase.query.dto.response.ColumnPurchaseHistoryListResponse;
import com.newbit.newbitfeatureservice.purchase.query.service.ColumnPurchaseHistoryQueryService;
import com.newbit.newbitfeatureservice.security.model.CustomUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "구매관련 API", description = "칼럼 구매 이력 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/purchase/column/history")
public class ColumnPurchaseHistoryQueryController {

    private final ColumnPurchaseHistoryQueryService columnPurchaseHistoryQueryService;

    @Operation(summary = "칼럼 구매 내역 조회", description = "특정 유저의 칼럼 구매 내역을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<ColumnPurchaseHistoryListResponse>> getColumnPurchaseHistory(
            @AuthenticationPrincipal CustomUser customUser,
            @ModelAttribute HistoryRequest requestDto) {
        requestDto.setUserId(customUser.getUserId());
        ColumnPurchaseHistoryListResponse response = columnPurchaseHistoryQueryService.getColumnPurchaseHistories(requestDto);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}