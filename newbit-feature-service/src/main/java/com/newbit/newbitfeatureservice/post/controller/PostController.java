package com.newbit.newbitfeatureservice.post.controller;

import com.newbit.newbitfeatureservice.security.model.CustomUser;
import com.newbit.newbitfeatureservice.post.dto.request.PostCreateRequest;
import com.newbit.newbitfeatureservice.post.dto.request.PostUpdateRequest;
import com.newbit.newbitfeatureservice.post.dto.response.PostDetailResponse;
import com.newbit.newbitfeatureservice.post.dto.response.PostResponse;
import com.newbit.newbitfeatureservice.post.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "게시글 API", description = "게시글 등록, 수정, 삭제, 조회 관련")
@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
@SecurityRequirement(name = "bearerAuth")
public class PostController {

    private final PostService postService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    @Operation(summary = "게시글 등록", description = "일반 사용자만 게시글을 등록할 수 있습니다.")
    public ResponseEntity<PostResponse> createPost(
            @RequestBody @Valid PostCreateRequest request,
            @AuthenticationPrincipal CustomUser user
    ) {
        PostResponse response = postService.createPost(request, user);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/search")
    @Operation(summary = "게시글 검색", description = "키워드를 통해 게시글 제목 또는 내용에서 검색합니다.")
    public ResponseEntity<List<PostResponse>> searchPosts(@RequestParam("keyword") String keyword) {
        List<PostResponse> responses = postService.searchPosts(keyword);
        return ResponseEntity.ok(responses);
    }

    @GetMapping
    @Operation(summary = "게시글 목록 조회", description = "페이징 정보를 기반으로 게시글 목록을 조회합니다.")
    public ResponseEntity<Page<PostResponse>> getPostList(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        Page<PostResponse> responses = postService.getPostList(pageable);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{postId}")
    @Operation(summary = "게시글 상세 조회", description = "게시글 상세정보를 조회합니다.")
    public ResponseEntity<PostDetailResponse> getPostDetail(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.getPostDetail(postId));
    }

    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PostResponse>> getMyPosts(@AuthenticationPrincipal CustomUser user) {
        List<PostResponse> myPosts = postService.getMyPosts(user.getUserId());
        return ResponseEntity.ok(myPosts);
    }

    @GetMapping("/popular")
    @Operation(summary = "인기 게시글 조회", description = "좋아요 10개 이상 받은 게시글을 좋아요 순으로 조회합니다.")
    public ResponseEntity<List<PostResponse>> getPopularPosts() {
        List<PostResponse> responses = postService.getPopularPosts();
        return ResponseEntity.ok(responses);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    @Operation(summary = "게시글 수정", description = "본인이 작성한 게시글의 제목과 내용을 수정합니다.")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable Long id,
            @RequestBody @Valid PostUpdateRequest request,
            @AuthenticationPrincipal CustomUser user
    ) {
        PostResponse response = postService.updatePost(id, request, user);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    @Operation(
            summary = "게시글 삭제",
            description = "본인이 작성한 게시글을 소프트 딜리트 방식으로 삭제합니다.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "삭제 성공"),
                    @ApiResponse(responseCode = "403", description = "작성자 외 사용자 접근 시 실패"),
                    @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음")
            }
    )
    public ResponseEntity<Void> deletePost(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUser user
    ) {
        postService.deletePost(id, user);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/notices")
    @Operation(summary = "공지사항 등록", description = "관리자만 공지사항을 등록할 수 있습니다.")
    public ResponseEntity<PostResponse> createNotice(
            @RequestBody @Valid PostCreateRequest request,
            @AuthenticationPrincipal CustomUser user
    ) {
        PostResponse response = postService.createNotice(request, user);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/notices/{postId}")
    @Operation(summary = "공지사항 수정", description = "관리자가 기존 공지사항을 수정합니다.")
    public ResponseEntity<PostResponse> updateNotice(
            @PathVariable Long postId,
            @RequestBody @Valid PostUpdateRequest request,
            @AuthenticationPrincipal CustomUser user
    ) {
        PostResponse response = postService.updateNotice(postId, request, user);
        return ResponseEntity.ok(response);
    }


    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/notices/{postId}")
    @Operation(summary = "공지사항 삭제", description = "관리자가 공지사항을 삭제합니다.")
    public ResponseEntity<Void> deleteNotice(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUser user
    ) {
        postService.deleteNotice(postId, user);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{postId}/like")
    @Operation(summary = "게시글 좋아요 증가")
    public ResponseEntity<Void> likePost(@PathVariable Long postId) {
        postService.increaseLikeCount(postId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{postId}/like")
    @Operation(summary = "게시글 좋아요 취소")
    public ResponseEntity<Void> unlikePost(@PathVariable Long postId) {
        postService.decreaseLikeCount(postId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{postId}/report")
    @Operation(summary = "게시글 신고")
    public ResponseEntity<Void> reportPost(@PathVariable Long postId) {
        postService.increaseReportCount(postId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{postId}/reports")
    @Operation(summary = "게시글 누적 신고 수 조회", description = "해당 게시글의 누적 신고 횟수를 반환합니다.")
    public ResponseEntity<Integer> getReportCountByPostId(@PathVariable Long postId) {
        int reportCount = postService.getReportCountByPostId(postId);
        return ResponseEntity.ok(reportCount);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{postId}/writer")
    @Operation(summary = "게시글 작성자 조회", description = "postId를 통해 작성자의 userId를 조회합니다.")
    public ResponseEntity<Long> getWriterIdByPostId(@PathVariable Long postId) {
        Long writerId = postService.getWriterIdByPostId(postId);
        return ResponseEntity.ok(writerId);
    }
}
