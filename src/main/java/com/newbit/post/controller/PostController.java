package com.newbit.post.controller;

import com.newbit.post.entity.Post;
import com.newbit.post.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class PostController {

    @Autowired
    private PostService postService;

    // 글 작성 폼
    @GetMapping("/post/write")
    public String postWriteForm() {
        return "postwrite"; // postwrite.html 템플릿과 연결
    }

    // 글 작성 처리
    @PostMapping("/post/writepro")
    public String postWrite(Post post, Model model) {
        postService.savePost(post);  // 파일 없이 저장하는 메서드
        model.addAttribute("message", "글 작성이 완료되었습니다.");
        model.addAttribute("searchUrl", "/post/list");  // 예시로 목록 페이지로 가게 함
        return "message"; // message.html에서 알림 처리
    }
}
