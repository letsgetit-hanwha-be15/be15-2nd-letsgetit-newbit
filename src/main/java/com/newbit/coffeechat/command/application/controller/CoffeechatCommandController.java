package com.newbit.coffeechat.command.application.controller;

import com.newbit.auth.model.CustomUser;
import com.newbit.coffeechat.command.application.dto.request.CoffeechatCreateRequest;
import com.newbit.coffeechat.command.application.dto.response.CoffeechatCommandResponse;
import com.newbit.coffeechat.command.application.service.CoffeechatCommandService;
import com.newbit.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/coffeechats")
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
}
