package com.newbit.post.service;

import com.newbit.auth.model.CustomUser;
import com.newbit.post.dto.request.CommentCreateRequest;
import com.newbit.post.dto.response.CommentResponse;
import com.newbit.post.entity.Comment;
import com.newbit.post.entity.Post;
import com.newbit.post.repository.CommentRepository;
import com.newbit.post.repository.PostRepository;
import com.newbit.purchase.command.application.service.PointTransactionCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final PointTransactionCommandService pointTransactionCommandService;

    @Transactional
    public CommentResponse createComment(Long postId, CommentCreateRequest request, CustomUser user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        Comment comment = Comment.builder()
                .content(request.getContent())
                .userId(user.getUserId()) // 인증 사용자 ID 사용
                .reportCount(0)
                .post(post)
                .build();

        commentRepository.save(comment);
        pointTransactionCommandService.givePointByType(user.getUserId(), "댓글 적립", comment.getId());
        return new CommentResponse(comment);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsByPostId(Long postId) {
        List<Comment> comments = commentRepository.findByPostIdAndDeletedAtIsNull(postId);
        return comments.stream()
                .map(CommentResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteComment(Long postId, Long commentId, CustomUser user) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));

        if (!comment.getPost().getId().equals(postId)) {
            throw new IllegalArgumentException("해당 댓글이 존재하지 않거나 게시글과 매칭되지 않습니다.");
        }


        if (!comment.getUserId().equals(user.getUserId())) {
            throw new SecurityException("댓글은 작성자만 삭제할 수 있습니다.");
        }
        comment.softDelete();
    }

}
