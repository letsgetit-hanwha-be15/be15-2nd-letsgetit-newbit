package com.newbit.post.controller;

import com.newbit.post.dto.PostCreateRequest;
import com.newbit.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<String> createPost(@RequestBody PostCreateRequest request) {
        postService.createPost(request);
        return ResponseEntity.ok("게시글이 등록되었습니다.");
    }
}
