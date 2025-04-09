package com.newbit.post.controller;

import com.newbit.post.dto.request.PostUpdateRequest;
import com.newbit.post.dto.request.PostCreateRequest;
import com.newbit.post.dto.response.PostResponse;
import com.newbit.post.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    @PutMapping("/{id}")
    @Operation(summary = "게시글 수정", description = "기존 게시글의 제목과 내용을 수정합니다.")
    public ResponseEntity<PostResponse> updatePost(@PathVariable Long id, @RequestBody PostUpdateRequest request) {
        PostResponse response = postService.updatePost(id, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "게시글 등록", description = "게시글을 등록하고 결과를 반환합니다.")
    public ResponseEntity<PostResponse> createPost(@RequestBody PostCreateRequest request) {
        PostResponse response = postService.createPost(request);
        return ResponseEntity.ok(response);
    }
}
