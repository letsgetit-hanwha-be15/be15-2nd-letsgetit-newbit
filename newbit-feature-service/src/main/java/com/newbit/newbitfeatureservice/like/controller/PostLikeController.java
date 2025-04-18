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

import com.newbit.newbitfeatureservice.like.dto.request.PostLikeRequest;
import com.newbit.newbitfeatureservice.like.dto.response.PostLikeResponse;
import com.newbit.newbitfeatureservice.like.service.PostLikeService;

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
@RequestMapping("/likes/posts")
@RequiredArgsConstructor
@Tag(name = "좋아요(게시글) API", description = "게시글 좋아요 관련 API")
public class PostLikeController {

    private final PostLikeService postLikeService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "게시글 좋아요 토글",
            description = "게시글에 좋아요를 추가하거나 취소합니다. 이미 좋아요가 있으면 취소, 없으면 추가됩니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200", 
                            description = "좋아요 토글 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PostLikeResponse.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "존재하지 않는 게시글"),
                    @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
            }
    )
    public ResponseEntity<PostLikeResponse> toggleLike(
            @Parameter(description = "게시글 좋아요 요청 정보", required = true)
            @RequestBody PostLikeRequest request,
            @Parameter(description = "인증된 사용자 정보", required = true)
            @AuthenticationPrincipal CustomUser user
    ) {
        PostLikeResponse response = postLikeService.toggleLike(request.getPostId(), user.getUserId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{postId}/status")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "게시글 좋아요 여부 확인",
            description = "로그인한 사용자가 특정 게시글에 좋아요를 눌렀는지 확인합니다.",
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
            @Parameter(description = "게시글 ID", required = true, example = "1")
            @PathVariable Long postId,
            @Parameter(description = "인증된 사용자 정보", required = true)
            @AuthenticationPrincipal CustomUser user
    ) {
        return ResponseEntity.ok(postLikeService.isLiked(postId, user.getUserId()));
    }

    @GetMapping("/{postId}/count")
    @Operation(
            summary = "게시글 좋아요 수 조회",
            description = "특정 게시글의 좋아요 수를 조회합니다.",
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
            @Parameter(description = "게시글 ID", required = true, example = "1")
            @PathVariable Long postId
    ) {
        int count = postLikeService.getLikeCount(postId);
        return ResponseEntity.ok(new LikeCountResponse(postId, count));
    }
    
    @Getter
    @AllArgsConstructor
    @Schema(description = "게시글 좋아요 수 응답")
    public static class LikeCountResponse {
        @Schema(description = "게시글 ID", example = "1")
        private Long postId;
        
        @Schema(description = "좋아요 수", example = "42")
        private int likeCount;
    }
} 