package com.newbit.newbitfeatureservice.purchase.query.controller;

import com.newbit.newbitfeatureservice.common.dto.ApiResponse;
import com.newbit.newbitfeatureservice.purchase.query.dto.request.HistoryRequest;
import com.newbit.newbitfeatureservice.purchase.query.dto.response.SaleHistoryListResponse;
import com.newbit.newbitfeatureservice.purchase.query.service.SaleHistoryQueryService;
import com.newbit.newbitfeatureservice.security.model.CustomUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "구매관련 API", description = "멘토 판매 내역 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/purchase/sale/history")
public class SaleHistoryQueryController {

    private final SaleHistoryQueryService saleHistoryQueryService;

    @Operation(summary = "판매 내역 조회", description = "특정 멘토의 판매 내역을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<SaleHistoryListResponse>> getSaleHistory(
            @AuthenticationPrincipal CustomUser customUser,
            @ModelAttribute HistoryRequest requestDto) {
        requestDto.setUserId(customUser.getUserId());
        SaleHistoryListResponse response = saleHistoryQueryService.getSaleHistories(requestDto);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}