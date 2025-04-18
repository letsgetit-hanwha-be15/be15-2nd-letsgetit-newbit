package com.newbit.newbitfeatureservice.coffeechat.command.application.controller;

import com.newbit.newbitfeatureservice.coffeechat.command.application.dto.request.ReviewCreateRequest;
import com.newbit.newbitfeatureservice.coffeechat.command.application.dto.response.ReviewCommandResponse;
import com.newbit.newbitfeatureservice.coffeechat.command.application.service.ReviewCommandService;
import com.newbit.newbitfeatureservice.common.dto.ApiResponse;
import com.newbit.newbitfeatureservice.security.model.CustomUser;
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
@RequestMapping("/review")
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
        Long reviewId = reviewCommandService.createReview(userId, reviewCreateRequest);

        ReviewCommandResponse response = ReviewCommandResponse.builder()
                .reviewId(reviewId)
                .build();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

    @Operation(
            summary = "리뷰 삭제",
            description = "사용자가 본인이 작성한 리뷰를 삭제합니다."
    )
    @PutMapping("/delete/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewCommandResponse>> deleteCoffeechat(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal CustomUser customUser
    ) {

        Long userId = customUser.getUserId();
        reviewCommandService.deleteReview(userId, reviewId);

        return ResponseEntity
                .ok(ApiResponse.success(null));
    }
}
