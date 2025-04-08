package com.newbit.purchase.command.application.controller;

import com.newbit.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/purchase")
@Tag(name = "구매 관련 API", description = "칼럼 구매 API")
public class PurchaseCommandController {
    private final PurchaseCommandService purchaseCommandService;

    @Operation(
            summary = "칼럼 구매",
            description = "사용자가 유료 칼럼을 구매합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "칼럼 구매 성공"
    )
    @PostMapping("/column")
    public ResponseEntity<ApiResponse<Void>> purchaseColumn(@Valid @RequestBody ColumnPurchaseRequest request) {
        purchaseCommandService.purchaseColumn(request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
