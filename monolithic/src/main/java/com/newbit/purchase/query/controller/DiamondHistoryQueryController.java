// DiamondHistoryQueryController.java
package com.newbit.purchase.query.controller;

import com.newbit.auth.model.CustomUser;
import com.newbit.common.dto.ApiResponse;
import com.newbit.purchase.query.dto.request.HistoryRequest;
import com.newbit.purchase.query.dto.response.AssetHistoryListResponse;
import com.newbit.purchase.query.service.DiamondHistoryQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "구매관련 API", description = "사용자 다이아 내역 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/purchase/diamond/history")
public class DiamondHistoryQueryController {

    private final DiamondHistoryQueryService diamondHistoryQueryService;

    @Operation(summary = "다이아 내역 조회", description = "지정된 사용자의 다이아 사용/획득 내역을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<AssetHistoryListResponse>> getDiamondHistories(
            @AuthenticationPrincipal CustomUser customUser,
            @ModelAttribute HistoryRequest requestDto) {
        requestDto.setUserId(customUser.getUserId());
        AssetHistoryListResponse response = diamondHistoryQueryService.getDiamondHistories(requestDto);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}