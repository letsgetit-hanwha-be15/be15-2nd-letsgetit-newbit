package com.newbit.post.controller;

import com.newbit.post.dto.PostDto;
import com.newbit.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    @PutMapping("/{id}")
    public ResponseEntity<String> post(@PathVariable Long id, @RequestBody PostDto request) {
        postService.post(id, request.getTitle(), request.getContent());
        return ResponseEntity.ok("게시글이 수정되었습니다.");
    }
}
