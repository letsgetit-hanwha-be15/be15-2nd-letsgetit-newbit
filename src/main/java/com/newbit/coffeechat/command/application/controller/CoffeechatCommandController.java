package com.newbit.coffeechat.command.application.controller;

import com.newbit.coffeechat.command.application.dto.request.CoffeechatCreateRequest;
import com.newbit.coffeechat.command.application.dto.response.CoffeechatCommandResponse;
import com.newbit.coffeechat.command.application.service.CoffeechatCommandService;
import com.newbit.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/coffeechat")
public class CoffeechatCommandController {

    private final CoffeechatCommandService coffeechatCommandService;

    @PostMapping("")
    public ResponseEntity<ApiResponse<CoffeechatCommandResponse>> createCoffeechat(
            @Validated CoffeechatCreateRequest coffeechatCreateRequest
    ) {

        Long coffeechatId = coffeechatCommandService.createCoffeechat(coffeechatCreateRequest);

        CoffeechatCommandResponse response = CoffeechatCommandResponse.builder()
                .coffeechatId(coffeechatId)
                .build();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }
}
