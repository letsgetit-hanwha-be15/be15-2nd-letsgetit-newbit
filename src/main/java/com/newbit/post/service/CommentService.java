package com.newbit.post.service;

import com.newbit.post.entity.Comment;
import com.newbit.post.entity.Post;
import com.newbit.post.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostService postService;  // PostService 주입

    // 특정 게시글에 대한 댓글 목록 조회
    public List<Comment> getComments(Integer postId) {
        Post post = postService.postGetById(postId); // Post 객체 가져오기
        return commentRepository.findByPost(post);   // Post 기준으로 댓글 조회
    }

    // 댓글 저장
    public void addComment(Comment comment) {
        commentRepository.save(comment);
    }
}
