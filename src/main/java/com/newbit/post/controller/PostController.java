package com.newbit.post.controller;

import com.newbit.post.entity.Post;
import com.newbit.post.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class PostController {

    @Autowired
    private PostService postService;

    // 글 작성 폼
    @GetMapping("/post/write")
    public String postWriteForm() {
        return "postwrite"; // postwrite.html 템플릿과 연결됨
    }

    // 글 작성 처리
    @PostMapping("/post/writepro")
    public String postWrite(Post post, Model model, MultipartFile file) throws Exception {
        postService.postWrite(post, file);
        model.addAttribute("message", "글 작성이 완료되었습니다.");
        model.addAttribute("searchUrl", "/post/write");
        return "message"; // message.html 템플릿에서 결과 알림 처리
    }
}
