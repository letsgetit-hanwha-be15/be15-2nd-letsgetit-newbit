package com.newbit.coffeechat.command.application.controller;

import com.newbit.auth.model.CustomUser;
import com.newbit.coffeechat.command.application.dto.request.CoffeechatCreateRequest;
import com.newbit.coffeechat.command.application.dto.request.ReviewCreateRequest;
import com.newbit.coffeechat.command.application.dto.response.CoffeechatCommandResponse;
import com.newbit.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/review")
@Tag(name = "리뷰 API", description = "리뷰 등록, 수정, 삭제 API")
public class ReviewCommandController {

    private final ReviewCommandService reviewCommandService;


    @Operation(
            summary = "리뷰 등록",
            description = "커피챗 종료 후 사용자가 멘토에 대한 리뷰를 작성합니다. 별점은 필수, 리뷰내용과 팁은 옵션입니다."
    )
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<ReviewCommandResponse>> createCoffeechat(
            @Valid @RequestBody ReviewCreateRequest reviewCreateRequest,
            @AuthenticationPrincipal CustomUser customUser
    ) {

        Long userId = customUser.getUserId();
        Long coffeechatId = reviewCommandService.createCoffeechat(userId, reviewCreateRequest);

        ReviewCommandResponse response = ReviewCommandResponse.builder()
                .coffeechatId(coffeechatId)
                .build();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

}
