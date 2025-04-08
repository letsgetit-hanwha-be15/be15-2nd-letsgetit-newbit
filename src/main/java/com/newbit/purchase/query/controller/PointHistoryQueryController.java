
package com.newbit.purchase.query.controller;

import com.newbit.common.dto.ApiResponse;
import com.newbit.purchase.query.dto.request.HistoryRequest;
import com.newbit.purchase.query.dto.response.AssetHistoryListResponse;
import com.newbit.purchase.query.service.PointHistoryQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "사용자 포인트 내역", description = "사용자 포인트 내역 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/purchase/point/history")
public class PointHistoryQueryController {
    private final PointHistoryQueryService pointHistoryQueryService;

    @GetMapping("/{userId}")
    @Operation(summary = "포인트 내역 조회", description = "지정된 사용자의 포인트 사용/획득 내역을 조회합니다.")
    public ResponseEntity<ApiResponse<AssetHistoryListResponse>> getPointHistories(
            @Parameter(description = "조회할 유저 ID", required = true) @PathVariable Long userId,
            @ModelAttribute HistoryRequest requestDto
    ) {
        requestDto.setUserId(userId);
        AssetHistoryListResponse response = pointHistoryQueryService.getPointHistories(requestDto);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}