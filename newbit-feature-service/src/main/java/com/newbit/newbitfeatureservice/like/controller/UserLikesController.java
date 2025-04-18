package com.newbit.newbitfeatureservice.like.controller;

import com.newbit.newbitfeatureservice.security.model.CustomUser;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.newbit.newbitfeatureservice.like.dto.response.LikedColumnListResponse;
import com.newbit.newbitfeatureservice.like.dto.response.LikedPostListResponse;
import com.newbit.newbitfeatureservice.like.service.ColumnLikeService;
import com.newbit.newbitfeatureservice.like.service.PostLikeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users/likes")
@RequiredArgsConstructor
@Tag(name = "좋아요 조회 API", description = "사용자가 좋아요한 게시물과 칼럼 조회 API")
public class UserLikesController {

    private final PostLikeService postLikeService;
    private final ColumnLikeService columnLikeService;
    
    @GetMapping("/posts")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "내가 좋아요한 게시글 목록 조회",
            description = "로그인한 사용자가 좋아요한 게시글 목록을 페이지네이션하여 조회합니다. 최신순으로 정렬됩니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200", 
                            description = "좋아요한 게시글 목록 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = LikedPostListResponse.class)
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
            }
    )
    public ResponseEntity<LikedPostListResponse> getLikedPosts(
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "인증된 사용자 정보", required = true)
            @AuthenticationPrincipal CustomUser user
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        LikedPostListResponse response = postLikeService.getLikedPostsByUser(user.getUserId(), pageable);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/columns")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "내가 좋아요한 칼럼 목록 조회",
            description = "로그인한 사용자가 좋아요한 칼럼 목록을 페이지네이션하여 조회합니다. 최신순으로 정렬됩니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200", 
                            description = "좋아요한 칼럼 목록 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = LikedColumnListResponse.class)
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
            }
    )
    public ResponseEntity<LikedColumnListResponse> getLikedColumns(
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "인증된 사용자 정보", required = true)
            @AuthenticationPrincipal CustomUser user
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        LikedColumnListResponse response = columnLikeService.getLikedColumnsByUser(user.getUserId(), pageable);
        return ResponseEntity.ok(response);
    }
} 