package com.newbit.post.controller;

import com.newbit.auth.model.CustomUser;
import com.newbit.post.dto.request.CommentCreateRequest;
import com.newbit.post.dto.response.CommentResponse;
import com.newbit.post.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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


}
