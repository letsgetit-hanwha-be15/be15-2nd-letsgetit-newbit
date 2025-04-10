package com.newbit.post.controller;

import com.newbit.post.dto.request.CommentCreateRequest;
import com.newbit.post.dto.response.CommentResponse;
import com.newbit.post.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts/{postId}/comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    @Operation(summary = "댓글 등록", description = "게시글에 댓글을 등록합니다.")
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable Long postId,
            @RequestBody @Valid CommentCreateRequest request
    ) {
        CommentResponse response = commentService.createComment(postId, request);
        return ResponseEntity.ok(response);
    }
}
