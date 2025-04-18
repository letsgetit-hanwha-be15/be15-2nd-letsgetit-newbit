package com.newbit.newbitfeatureservice.coffeechat.command.application.controller;

import com.newbit.newbitfeatureservice.security.model.CustomUser;
import com.newbit.newbitfeatureservice.coffeechat.command.application.dto.request.CoffeechatCancelRequest;
import com.newbit.newbitfeatureservice.coffeechat.command.application.dto.request.CoffeechatCreateRequest;
import com.newbit.newbitfeatureservice.coffeechat.command.application.dto.response.CoffeechatCommandResponse;
import com.newbit.newbitfeatureservice.coffeechat.command.application.service.CoffeechatCommandService;
import com.newbit.newbitfeatureservice.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/coffeechats")
@Tag(name = "커피챗 API", description = "커피챗 등록, 수정, 삭제 API")
public class CoffeechatCommandController {

    private final CoffeechatCommandService coffeechatCommandService;

    @Operation(
            summary = "커피챗 등록",
            description = "사용자가 멘토에게 커피챗을 요청합니다."
    )
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<CoffeechatCommandResponse>> createCoffeechat(
            @Valid @RequestBody CoffeechatCreateRequest coffeechatCreateRequest,
            @AuthenticationPrincipal CustomUser customUser
    ) {

        Long userId = customUser.getUserId();
        Long coffeechatId = coffeechatCommandService.createCoffeechat(userId, coffeechatCreateRequest);

        CoffeechatCommandResponse response = CoffeechatCommandResponse.builder()
                .coffeechatId(coffeechatId)
                .build();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

    @Operation(
            summary = "커피챗 일정 승인",
            description = "멘토가 커피챗 일정을 승인합니다."
    )
    @PutMapping("/approve/{requestTimeId}")
//    @PreAuthorize("hasAuthority('MENTOR')")
    public ResponseEntity<ApiResponse<Void>> acceptCoffeechatTime(
            @PathVariable Long requestTimeId
    ) {

        coffeechatCommandService.acceptCoffeechatTime(requestTimeId);

        return ResponseEntity
                .ok(ApiResponse.success(null));
    }

    @Operation(
            summary = "커피챗 일정 거절",
            description = "멘토가 커피챗 일정을 거절합니다."
    )
    @PutMapping("/reject/{coffeechatId}")
//    @PreAuthorize("hasAuthority('MENTOR')")
    public ResponseEntity<ApiResponse<Void>> rejectCoffeechatTime(
            @PathVariable Long coffeechatId
    ) {

        coffeechatCommandService.rejectCoffeechatTime(coffeechatId);

        return ResponseEntity
                .ok(ApiResponse.success(null));
    }

    @Operation(
            summary = "커피챗 종료",
            description = "멘토가 커피챗을 종료합니다."
    )
    @PutMapping("/close/{coffeechatId}")
//    @PreAuthorize("hasAuthority('MENTOR')")
    public ResponseEntity<ApiResponse<Void>> closeCoffeechat(
            @PathVariable Long coffeechatId
    ) {

        coffeechatCommandService.closeCoffeechat(coffeechatId);

        return ResponseEntity
                .ok(ApiResponse.success(null));
    }

    @Operation(
            summary = "커피챗 구매확정",
            description = "멘티가 커피챗 구매확정합니다."
    )
    @PutMapping("/confirm-purchase/{coffeechatId}")
    public ResponseEntity<ApiResponse<Void>> confirmPurchaseCoffeechat(
            @PathVariable Long coffeechatId
    ) {

        coffeechatCommandService.confirmPurchaseCoffeechat(coffeechatId);

        return ResponseEntity
                .ok(ApiResponse.success(null));
    }

    @Operation(
            summary = "커피챗 취소",
            description = "멘티가 커피챗을 취소합니다."
    )
    @PutMapping("/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelCoffeechat(
            @Valid @RequestBody CoffeechatCancelRequest coffeechatCancelRequest,
            @AuthenticationPrincipal CustomUser customUser
    ) {

        Long userId = customUser.getUserId();
        coffeechatCommandService.cancelCoffeechat(userId, coffeechatCancelRequest);

        return ResponseEntity
                .ok(ApiResponse.success(null));
    }
}
