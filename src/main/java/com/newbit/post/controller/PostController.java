package com.newbit.post.controller;

import com.newbit.post.entity.Comment;
import com.newbit.post.entity.Post;
import com.newbit.post.service.CommentService;
import com.newbit.post.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    // 글 작성 폼
    @GetMapping("/post/write")
    public String postWriteForm() {
        return "postwrite";
    }

    // 글 작성 처리
    @PostMapping("/post/writepro")
    public String postWrite(Post post, Model model, MultipartFile file) throws Exception {
        postService.postWrite(post, file);
        model.addAttribute("message", "글 작성이 완료되었습니다.");
        model.addAttribute("searchUrl", "/post/list");
        return "message";
    }

    // 게시글 목록
    @GetMapping("/post/list")
    public String postList(Model model,
                           @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                           @RequestParam(required = false) String searchKeyword) {

        Page<Post> list = (searchKeyword == null)
                ? postService.postList(pageable)
                : postService.postSearch(searchKeyword, pageable);

        int nowPage = list.getNumber() + 1;
        int startPage = Math.max(nowPage - 4, 1);
        int endPage = Math.min(nowPage + 5, list.getTotalPages());

        model.addAttribute("list", list);
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "postList";
    }

    // 게시글 상세보기
    @GetMapping("/post/view")
    public String postView(Model model, @RequestParam("id") Integer id) {
        model.addAttribute("post", postService.postView(id));
        model.addAttribute("comments", commentService.getComments(id));
        return "postview";
    }

    // 게시글 삭제
//    @PostMapping("/post/delete/{id}")
//    public String postDelete(@PathVariable("id") Integer id) {
//        postService.postDelete(id);
//        return "redirect:/post/list";
//    }

    // 게시글 수정 폼
    @GetMapping("/post/modify/{id}")
    public String postModifyForm(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("post", postService.postView(id));
        return "postmodify";
    }

    // 게시글 수정 처리
    @PostMapping("/post/update/{id}")
    public String postUpdate(@PathVariable("id") Integer id, Post post, MultipartFile file) throws Exception {
        Post postTemp = postService.postView(id);
        postTemp.setTitle(post.getTitle());
        postTemp.setContent(post.getContent());

        if (file != null && !file.isEmpty()) {
            postService.postWrite(postTemp, file);
        } else {
            postService.postUpdateWithoutFile(postTemp);
        }

        return "redirect:/post/list";
    }

    // 좋아요 토글
    @GetMapping("/post/like/{id}")
    public String postLikeToggle(@PathVariable("id") Integer id) {
        String username = "testUser";
        postService.postLikeToggle(id, username);
        return "redirect:/post/view?id=" + id;
    }

    // 댓글 작성
    @PostMapping("/post/comment")
    public String postCommentWrite(@RequestParam("postId") Integer postId,
                                   @RequestParam("writer") String writer,
                                   @RequestParam("content") String content) {

        Comment comment = new Comment();
        comment.setPost(postService.postView(postId));
        comment.setWriter(writer);  // Comment 엔티티에 writer 필드가 있어야 함
        comment.setContent(content);
        commentService.addComment(comment);
        return "redirect:/post/view?id=" + postId;
    }
}
