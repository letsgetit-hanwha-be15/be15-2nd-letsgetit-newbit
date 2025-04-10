package com.newbit.coffeechat.command.application.controller;

import com.newbit.auth.model.CustomUser;
import com.newbit.coffeechat.command.application.dto.request.CoffeechatCreateRequest;
import com.newbit.coffeechat.command.application.dto.response.CoffeechatCommandResponse;
import com.newbit.coffeechat.command.application.service.CoffeechatCommandService;
import com.newbit.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/coffeechats")
public class CoffeechatCommandController {

    private final CoffeechatCommandService coffeechatCommandService;

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
