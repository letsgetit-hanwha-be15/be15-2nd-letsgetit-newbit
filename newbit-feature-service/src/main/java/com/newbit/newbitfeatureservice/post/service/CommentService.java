package com.newbit.newbitfeatureservice.post.service;

import com.newbit.newbitfeatureservice.client.user.UserFeignClient;
import com.newbit.newbitfeatureservice.client.user.dto.UserDTO;
import com.newbit.newbitfeatureservice.common.dto.ApiResponse;
import com.newbit.newbitfeatureservice.common.exception.BusinessException;
import com.newbit.newbitfeatureservice.common.exception.ErrorCode;
import com.newbit.newbitfeatureservice.notification.command.application.dto.request.NotificationSendRequest;
import com.newbit.newbitfeatureservice.notification.command.application.service.NotificationCommandService;
import com.newbit.newbitfeatureservice.post.dto.request.CommentCreateRequest;
import com.newbit.newbitfeatureservice.post.dto.response.CommentResponse;
import com.newbit.newbitfeatureservice.post.entity.Comment;
import com.newbit.newbitfeatureservice.post.repository.CommentRepository;
import com.newbit.newbitfeatureservice.post.repository.PostRepository;
import com.newbit.newbitfeatureservice.purchase.command.application.service.PointTransactionCommandService;
import com.newbit.newbitfeatureservice.purchase.command.domain.PointTypeConstants;
import com.newbit.newbitfeatureservice.security.model.CustomUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final CommentInternalService commentInternalService;
    private final UserFeignClient userFeignClient;

    public CommentResponse createComment(Long postId, CommentCreateRequest request, CustomUser user) {
        // 댓글 저장 및 응답은 트랜잭션 처리
        Comment comment = commentInternalService.saveCommentInternal(postId, request, user);

        // 트랜잭션 이후 외부 호출 (포인트 지급, 알림 전송)
        pointTransactionCommandService.givePointByType(user.getUserId(), PointTypeConstants.COMMENTS, comment.getId());

        String notificationContent = String.format("'%s' 게시글에 댓글이 달렸습니다. ('%s')",
                comment.getPost().getTitle(), comment.getContent());

        notificationCommandService.sendNotification(
                new NotificationSendRequest(
                        comment.getPost().getUserId(),
                        1L,
                        postId,
                        notificationContent
                )
        );

        // 작성자 닉네임 조회
        ApiResponse<UserDTO> response = userFeignClient.getUserByUserId(comment.getUserId());
        String writerName = response.getData() != null ? response.getData().getNickname() : null;

        return new CommentResponse(comment, writerName);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsByPostId(Long postId) {
        List<Comment> comments = commentRepository.findByPostIdAndDeletedAtIsNull(postId);

        return comments.stream()
                .map(comment -> {
                    ApiResponse<UserDTO> response = userFeignClient.getUserByUserId(comment.getUserId());
                    String writerName = response.getData() != null ? response.getData().getNickname() : null;
                    return new CommentResponse(comment, writerName);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<CommentResponse> getCommentsByPostId(Long postId, Pageable pageable) {
        Page<Comment> commentPage = commentRepository.findByPostIdAndDeletedAtIsNull(postId, pageable);

        return commentPage.map(comment -> {
            ApiResponse<UserDTO> response = userFeignClient.getUserByUserId(comment.getUserId());
            String writerName = response.getData() != null ? response.getData().getNickname() : null;
            return new CommentResponse(comment, writerName);
        });
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

    @Transactional(readOnly = true)
    public String getCommentContent(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));
        return comment.getContent();
    }

}
