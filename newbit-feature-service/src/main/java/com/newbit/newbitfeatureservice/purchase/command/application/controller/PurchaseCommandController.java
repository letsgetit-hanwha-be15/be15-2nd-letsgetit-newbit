package com.newbit.newbitfeatureservice.purchase.command.application.controller;

import com.newbit.newbitfeatureservice.common.dto.ApiResponse;
import com.newbit.newbitfeatureservice.purchase.command.application.dto.CoffeeChatPurchaseRequest;
import com.newbit.newbitfeatureservice.purchase.command.application.dto.ColumnPurchaseRequest;
import com.newbit.newbitfeatureservice.purchase.command.application.dto.MentorAuthorityPurchaseRequest;
import com.newbit.newbitfeatureservice.purchase.command.application.service.PurchaseCommandService;
import com.newbit.newbitfeatureservice.security.model.CustomUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/purchase")
@Tag(name = "구매관련 API", description = "칼럼 구매 API")
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
    public ResponseEntity<ApiResponse<Void>> purchaseColumn(
            @AuthenticationPrincipal CustomUser customUser,
            @Valid @RequestBody ColumnPurchaseRequest request
    ) {
        purchaseCommandService.purchaseColumn(customUser.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(
            summary = "커피챗 구매",
            description = "사용자가 멘토와 확정된 커피챗을 구매합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "커피챗 구매 성공"
    )
    @PostMapping("/coffeechat")
    public ResponseEntity<ApiResponse<Void>> purchaseCoffeeChat(
            @AuthenticationPrincipal CustomUser customUser,
            @Valid @RequestBody CoffeeChatPurchaseRequest request) {
        purchaseCommandService.purchaseCoffeeChat(customUser.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }


    @Operation(
            summary = "멘토 권한 구매",
            description = "사용자가 포인트 혹은 다이아를 사용하여 멘토 권한을 구매합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "멘토 권한 구매 성공"
    )
    @PostMapping("/mentor-authority")
    public ResponseEntity<ApiResponse<Void>> purchaseMentorAuthority(
            @AuthenticationPrincipal CustomUser customUser,
            @Valid @RequestBody MentorAuthorityPurchaseRequest request
    ) {
        purchaseCommandService.purchaseMentorAuthority(customUser.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

}
