// DiamondHistoryQueryController.java
package com.newbit.newbitfeatureservice.purchase.query.controller;

import com.newbit.newbitfeatureservice.common.dto.ApiResponse;
import com.newbit.newbitfeatureservice.purchase.query.dto.request.HistoryRequest;
import com.newbit.newbitfeatureservice.purchase.query.dto.response.AssetHistoryListResponse;
import com.newbit.newbitfeatureservice.purchase.query.service.DiamondHistoryQueryService;
import com.newbit.newbitfeatureservice.security.model.CustomUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "구매관련 API", description = "사용자 다이아 내역 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/purchase/diamond/history")
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