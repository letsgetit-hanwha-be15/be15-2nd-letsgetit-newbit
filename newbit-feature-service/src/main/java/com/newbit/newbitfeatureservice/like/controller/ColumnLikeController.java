package com.newbit.newbitfeatureservice.like.controller;

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

import com.newbit.newbitfeatureservice.like.dto.request.ColumnLikeRequest;
import com.newbit.newbitfeatureservice.like.dto.response.ColumnLikeResponse;
import com.newbit.newbitfeatureservice.like.service.ColumnLikeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Getter;

@RestController
@RequestMapping("/likes/columns")
@RequiredArgsConstructor
@Tag(name = "좋아요(칼럼) API", description = "칼럼 좋아요 관련 API")
public class ColumnLikeController {

    private final ColumnLikeService columnLikeService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "칼럼 좋아요 토글",
            description = "칼럼에 좋아요를 추가하거나 취소합니다. 이미 좋아요가 있으면 취소, 없으면 추가됩니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200", 
                            description = "좋아요 토글 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ColumnLikeResponse.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "존재하지 않는 칼럼"),
                    @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
            }
    )
    public ResponseEntity<ColumnLikeResponse> toggleLike(
            @Parameter(description = "칼럼 좋아요 요청 정보", required = true)
            @RequestBody ColumnLikeRequest request,
            @Parameter(description = "인증된 사용자 정보", required = true)
            @AuthenticationPrincipal CustomUser user
    ) {
        ColumnLikeResponse response = columnLikeService.toggleLike(request.getColumnId(), user.getUserId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{columnId}/status")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "칼럼 좋아요 여부 확인",
            description = "로그인한 사용자가 특정 칼럼에 좋아요를 눌렀는지 확인합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200", 
                            description = "좋아요 여부 확인 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "boolean", description = "좋아요 여부 (true: 좋아요 누름, false: 좋아요 안누름)")
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
            }
    )
    public ResponseEntity<Boolean> isLiked(
            @Parameter(description = "칼럼 ID", required = true, example = "1")
            @PathVariable Long columnId,
            @Parameter(description = "인증된 사용자 정보", required = true)
            @AuthenticationPrincipal CustomUser user
    ) {
        return ResponseEntity.ok(columnLikeService.isLiked(columnId, user.getUserId()));
    }

    @GetMapping("/{columnId}/count")
    @Operation(
            summary = "칼럼 좋아요 수 조회",
            description = "특정 칼럼의 좋아요 수를 조회합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200", 
                            description = "좋아요 수 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = LikeCountResponse.class)
                            )
                    )
            }
    )
    public ResponseEntity<LikeCountResponse> getLikeCount(
            @Parameter(description = "칼럼 ID", required = true, example = "1")
            @PathVariable Long columnId
    ) {
        int count = columnLikeService.getLikeCount(columnId);
        return ResponseEntity.ok(new LikeCountResponse(columnId, count));
    }
    
    @Getter
    @AllArgsConstructor
    @Schema(description = "칼럼 좋아요 수 응답")
    public static class LikeCountResponse {
        @Schema(description = "칼럼 ID", example = "1")
        private Long columnId;
        
        @Schema(description = "좋아요 수", example = "42")
        private int likeCount;
    }
} 