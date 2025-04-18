package com.newbit.newbitfeatureservice.post.controller;

import com.newbit.newbitfeatureservice.post.dto.request.CommentCreateRequest;
import com.newbit.newbitfeatureservice.post.dto.response.CommentResponse;
import com.newbit.newbitfeatureservice.post.service.CommentService;
import com.newbit.newbitfeatureservice.security.model.CustomUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "댓글 API", description = "댓글 등록, 조회, 삭제 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/posts/{postId}/comments")
public class CommentController {

    private final CommentService commentService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    @Operation(summary = "댓글 등록", description = "게시글에 댓글을 등록합니다.")
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable Long postId,
            @RequestBody @Valid CommentCreateRequest request,
            @AuthenticationPrincipal CustomUser user
    ) {
        CommentResponse response = commentService.createComment(postId, request, user);
        return ResponseEntity.ok(response);
    }


    @GetMapping
    @Operation(summary = "댓글 조회", description = "게시글에 달린 댓글 목록을 조회합니다.")
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Long postId) {
        List<CommentResponse> responses = commentService.getCommentsByPostId(postId);
        return ResponseEntity.ok(responses);
    }


    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{commentId}")
    @Operation(summary = "댓글 삭제", description = "회원만 자신이 작성한 댓글을 삭제할 수 있습니다.")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal CustomUser user
    ) {
        commentService.deleteComment(postId, commentId, user);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{commentId}/report")
    @Operation(summary = "댓글 신고", description = "댓글에 대해 신고 처리를 합니다.")
    public ResponseEntity<Void> reportComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal CustomUser user
    ) {
        commentService.increaseReportCount(commentId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{commentId}/report")
    @Operation(summary = "댓글 신고 수 조회", description = "해당 댓글의 신고 횟수를 반환합니다.")
    public ResponseEntity<Integer> getReportCount(@PathVariable Long commentId) {
        int reportCount = commentService.getReportCount(commentId);
        return ResponseEntity.ok(reportCount);
    }

}
