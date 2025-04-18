package com.newbit.post.service;

import com.newbit.auth.model.CustomUser;
import com.newbit.common.exception.BusinessException;
import com.newbit.common.exception.ErrorCode;
import com.newbit.notification.command.application.dto.request.NotificationSendRequest;
import com.newbit.notification.command.application.service.NotificationCommandService;
import com.newbit.notification.command.domain.repository.NotificationRepository;
import com.newbit.post.dto.request.CommentCreateRequest;
import com.newbit.post.dto.response.CommentResponse;
import com.newbit.post.entity.Comment;
import com.newbit.post.entity.Post;
import com.newbit.post.repository.CommentRepository;
import com.newbit.post.repository.PostRepository;
import com.newbit.purchase.command.application.service.PointTransactionCommandService;
import com.newbit.purchase.command.domain.PointTypeConstants;
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
    private final NotificationCommandService notificationCommandService;

    @Transactional
    public CommentResponse createComment(Long postId, CommentCreateRequest request, CustomUser user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        Comment comment = Comment.builder()
                .content(request.getContent())
                .userId(user.getUserId())
                .reportCount(0)
                .post(post)
                .build();

        commentRepository.save(comment);
        pointTransactionCommandService.givePointByType(user.getUserId(), PointTypeConstants.COMMENTS, comment.getId());

        String notificationContent = String.format("'%s' 게시글에 댓글이 달렸습니다. ('%s')",
                post.getTitle(), comment.getContent());

        notificationCommandService.sendNotification(
                new NotificationSendRequest(
                        post.getUserId()
                        , 1L
                        , postId
                        , notificationContent
                )
        );

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
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));

        if (!comment.getPost().getId().equals(postId)) {
            throw new BusinessException(ErrorCode.COMMENT_POST_MISMATCH);
        }

        if (!comment.getUserId().equals(user.getUserId())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_TO_DELETE_COMMENT);
        }

        comment.softDelete();
    }

    @Transactional
    public void deleteCommentAsAdmin(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));
        comment.softDelete();
    }
    
    @Transactional(readOnly = true)
    public Long getPostIdByCommentId(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));
        return comment.getPost().getId();
    }

    @Transactional
    public void increaseReportCount(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));
        comment.increaseReportCount(); 
    }

    @Transactional(readOnly = true)
    public int getReportCount(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));
        return comment.getReportCount();
    }

}
