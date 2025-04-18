
package com.newbit.newbitfeatureservice.purchase.query.controller;

import com.newbit.newbitfeatureservice.common.dto.ApiResponse;
import com.newbit.newbitfeatureservice.purchase.query.dto.request.HistoryRequest;
import com.newbit.newbitfeatureservice.purchase.query.dto.response.AssetHistoryListResponse;
import com.newbit.newbitfeatureservice.purchase.query.service.PointHistoryQueryService;
import com.newbit.newbitfeatureservice.security.model.CustomUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "구매관련 API", description = "사용자 포인트 내역 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/purchase/point/history")
public class PointHistoryQueryController {
    private final PointHistoryQueryService pointHistoryQueryService;

    @GetMapping
    @Operation(summary = "포인트 내역 조회", description = "지정된 사용자의 포인트 사용/획득 내역을 조회합니다.")
    public ResponseEntity<ApiResponse<AssetHistoryListResponse>> getPointHistories(
            @AuthenticationPrincipal CustomUser customUser,
            @ModelAttribute HistoryRequest requestDto
    ) {
        requestDto.setUserId(customUser.getUserId());
        AssetHistoryListResponse response = pointHistoryQueryService.getPointHistories(requestDto);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}