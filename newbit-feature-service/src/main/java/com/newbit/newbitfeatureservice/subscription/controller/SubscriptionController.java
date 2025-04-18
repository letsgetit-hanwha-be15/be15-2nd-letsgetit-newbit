package com.newbit.newbitfeatureservice.subscription.controller;

import java.util.List;

import com.newbit.newbitfeatureservice.security.model.CustomUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.newbit.newbitfeatureservice.subscription.dto.request.SubscriptionRequest;
import com.newbit.newbitfeatureservice.subscription.dto.response.SubscriptionResponse;
import com.newbit.newbitfeatureservice.subscription.dto.response.SubscriptionStatusResponse;
import com.newbit.newbitfeatureservice.subscription.service.SubscriptionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Getter;

@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
@Tag(name = "시리즈 구독 API", description = "시리즈 구독 관련 API")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "시리즈 구독 토글",
            description = "시리즈 구독을 추가하거나 취소합니다. 이미 구독 중이면 취소, 구독 중이 아니면 구독 추가됩니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200", 
                            description = "구독 토글 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SubscriptionResponse.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "존재하지 않는 시리즈"),
                    @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
            }
    )
    public ResponseEntity<SubscriptionResponse> toggleSubscription(
            @Parameter(description = "시리즈 구독 요청 정보", required = true)
            @Valid @RequestBody SubscriptionRequest request,
            @Parameter(description = "인증된 사용자 정보", required = true)
            @AuthenticationPrincipal CustomUser user
    ) {
        SubscriptionResponse response = subscriptionService.toggleSubscription(request.getSeriesId(), user.getUserId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/series/{seriesId}/status")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "시리즈 구독 상태 확인",
            description = "로그인한 사용자가 특정 시리즈를 구독했는지 여부와 구독자 수를 확인합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200", 
                            description = "구독 상태 확인 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SubscriptionStatusResponse.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "존재하지 않는 시리즈"),
                    @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
            }
    )
    public ResponseEntity<SubscriptionStatusResponse> checkSubscriptionStatus(
            @Parameter(description = "시리즈 ID", required = true, example = "1")
            @PathVariable Long seriesId,
            @Parameter(description = "인증된 사용자 정보", required = true)
            @AuthenticationPrincipal CustomUser user
    ) {
        return ResponseEntity.ok(subscriptionService.getSubscriptionStatus(seriesId, user.getUserId()));
    }

    @GetMapping("/series/{seriesId}/count")
    @Operation(
            summary = "시리즈 구독자 수 조회",
            description = "특정 시리즈의 구독자 수를 조회합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200", 
                            description = "구독자 수 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SubscriberCountResponse.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "존재하지 않는 시리즈")
            }
    )
    public ResponseEntity<SubscriberCountResponse> getSubscriberCount(
            @Parameter(description = "시리즈 ID", required = true, example = "1")
            @PathVariable Long seriesId
    ) {
        int count = subscriptionService.getSubscriberCount(seriesId);
        return ResponseEntity.ok(new SubscriberCountResponse(seriesId, count));
    }
    
    @GetMapping("/user/list")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "내 구독 목록 조회",
            description = "로그인한 사용자가 구독 중인 시리즈 목록을 조회합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200", 
                            description = "구독 목록 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = SubscriptionResponse.class))
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
            }
    )
    public ResponseEntity<List<SubscriptionResponse>> getMySubscriptions(
            @Parameter(description = "인증된 사용자 정보", required = true)
            @AuthenticationPrincipal CustomUser user
    ) {
        return ResponseEntity.ok(subscriptionService.getUserSubscriptions(user.getUserId()));
    }
    
    @Getter
    @AllArgsConstructor
    @Schema(description = "구독자 수 응답")
    public static class SubscriberCountResponse {
        @Schema(description = "시리즈 ID", example = "1")
        private Long seriesId;
        
        @Schema(description = "구독자 수", example = "325")
        private int subscriberCount;
    }
} 