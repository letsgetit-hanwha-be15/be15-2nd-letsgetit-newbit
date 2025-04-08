
package com.newbit.purchase.query.controller;

import com.newbit.common.dto.ApiResponse;
import com.newbit.purchase.query.dto.request.HistoryRequest;
import com.newbit.purchase.query.dto.response.AssetHistoryListResponse;
import com.newbit.purchase.query.service.PointHistoryQueryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "사용자 포인트 내역", description = "사용자 포인트 내역 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/purchase/point/history")
public class PointHistoryQueryController {
    private final PointHistoryQueryService pointHistoryQueryService;

    @GetMapping
    public ResponseEntity<ApiResponse<AssetHistoryListResponse>> getPointHistories(@ModelAttribute HistoryRequest requestDto) {
        AssetHistoryListResponse response = pointHistoryQueryService.getPointHistories(requestDto);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}