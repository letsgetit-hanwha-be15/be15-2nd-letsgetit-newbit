package com.newbit.like.service;

import java.util.Optional;

import com.newbit.notification.command.application.dto.request.NotificationSendRequest;
import com.newbit.notification.command.application.service.NotificationCommandService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newbit.common.exception.BusinessException;
import com.newbit.common.exception.ErrorCode;
import com.newbit.column.domain.Column;
import com.newbit.column.repository.ColumnRepository;
import com.newbit.like.dto.response.ColumnLikeResponse;
import com.newbit.like.dto.response.PostLikeResponse;
import com.newbit.like.entity.Like;
import com.newbit.like.repository.LikeRepository;
import com.newbit.post.entity.Post;
import com.newbit.post.repository.PostRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeCommandService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final ColumnRepository columnRepository;
    private final PointRewardService pointRewardService;
    private final NotificationCommandService notificationCommandService;

    @Transactional
    public PostLikeResponse togglePostLike(Long postId, Long userId) {
        try {
            Post post = findPostById(postId);
            Optional<Like> existingLike = findExistingPostLike(postId, userId);
            
            if (existingLike.isPresent()) {
                return unlikePost(existingLike.get(), post);
            } else {
                return likePost(postId, userId, post);
            }
        } catch (BusinessException e) {
            log.error("좋아요 처리 중 비즈니스 예외 발생: postId={}, userId={}, errorCode={}, message={}",
                    postId, userId, e.getErrorCode(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("좋아요 처리 중 예기치 않은 오류 발생: postId={}, userId={}, error={}", 
                    postId, userId, e.getMessage(), e);
            throw new BusinessException(ErrorCode.LIKE_PROCESSING_ERROR);
        }
    }
    
    @Transactional
    public ColumnLikeResponse toggleColumnLike(Long columnId, Long userId) {
        try {
            Column column = findColumnById(columnId);
            Optional<Like> existingLike = findExistingColumnLike(columnId, userId);
            
            if (existingLike.isPresent()) {
                return unlikeColumn(existingLike.get(), column);
            } else {
                return likeColumn(columnId, userId, column);
            }
        } catch (BusinessException e) {
            log.error("칼럼 좋아요 처리 중 비즈니스 예외 발생: columnId={}, userId={}, errorCode={}, message={}",
                    columnId, userId, e.getErrorCode(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("칼럼 좋아요 처리 중 예기치 않은 오류 발생: columnId={}, userId={}, error={}", 
                    columnId, userId, e.getMessage(), e);
            throw new BusinessException(ErrorCode.LIKE_PROCESSING_ERROR);
        }
    }

    private Post findPostById(Long postId) {
        return postRepository.findByIdAndDeletedAtIsNull(postId)
                .orElseThrow(() -> {
                    log.warn("좋아요 처리를 위한 게시글을 찾을 수 없음: postId={}", postId);
                    return new BusinessException(ErrorCode.POST_LIKE_NOT_FOUND);
                });
    }

    private Optional<Like> findExistingPostLike(Long postId, Long userId) {
        return likeRepository.findByPostIdAndUserIdAndIsDeleteFalse(postId, userId);
    }

    private PostLikeResponse unlikePost(Like like, Post post) {
        try {
            like.setDelete(true);
            likeRepository.save(like);
            
            decreaseLikeCount(post);
            
            return PostLikeResponse.unliked(post.getId(), like.getUserId(), post.getLikeCount());
        } catch (Exception e) {
            log.error("좋아요 취소 처리 중 오류 발생: postId={}, userId={}, error={}", 
                    post.getId(), like.getUserId(), e.getMessage(), e);
            throw new BusinessException(ErrorCode.LIKE_PROCESSING_ERROR);
        }
    }

    private PostLikeResponse likePost(Long postId, Long userId, Post post) {
        try {
            Like like = createPostLike(postId, userId);
            likeRepository.save(like);
            
            increaseLikeCount(post);
            
            pointRewardService.givePointIfFirstLike(postId, userId, post.getUserId());
            
            return PostLikeResponse.of(like, post.getLikeCount());
        } catch (Exception e) {
            log.error("좋아요 추가 처리 중 오류 발생: postId={}, userId={}, error={}", 
                    postId, userId, e.getMessage(), e);
            throw new BusinessException(ErrorCode.LIKE_PROCESSING_ERROR);
        }
    }

    private Like createPostLike(Long postId, Long userId) {
        return Like.builder()
                .postId(postId)
                .userId(userId)
                .isDelete(false)
                .build();
    }

    private void decreaseLikeCount(Post post) {
        post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
        postRepository.save(post);
    }

    private void increaseLikeCount(Post post) {
        post.setLikeCount(post.getLikeCount() + 1);
        postRepository.save(post);
    }
    
    private Column findColumnById(Long columnId) {
        return columnRepository.findById(columnId)
                .orElseThrow(() -> {
                    log.warn("좋아요 처리를 위한 칼럼을 찾을 수 없음: columnId={}", columnId);
                    return new BusinessException(ErrorCode.COLUMN_NOT_FOUND);
                });
    }
    
    private Optional<Like> findExistingColumnLike(Long columnId, Long userId) {
        return likeRepository.findByColumnIdAndUserIdAndIsDeleteFalse(columnId, userId);
    }
    
    private ColumnLikeResponse unlikeColumn(Like like, Column column) {
        try {
            like.setDelete(true);
            likeRepository.save(like);
            
            column.decreaseLikeCount();
            columnRepository.save(column);
            
            return ColumnLikeResponse.unliked(column.getColumnId(), like.getUserId(), column.getLikeCount());
        } catch (Exception e) {
            log.error("칼럼 좋아요 취소 처리 중 오류 발생: columnId={}, userId={}, error={}", 
                    column.getColumnId(), like.getUserId(), e.getMessage(), e);
            throw new BusinessException(ErrorCode.LIKE_PROCESSING_ERROR);
        }
    }
    
    private ColumnLikeResponse likeColumn(Long columnId, Long userId, Column column) {
        try {
            Like like = createColumnLike(columnId, userId);
            likeRepository.save(like);
            
            column.increaseLikeCount();
            columnRepository.save(column);

            if(isFibonacci(column.getLikeCount())){
                String notificationContent = String.format("'%s' 칼럼이 좋아요를 받았습니다. (총 %d개)",
                        column.getTitle(), column.getLikeCount());

                notificationCommandService.sendNotification(
                        new NotificationSendRequest(
                                column.getMentor().getUser().getUserId()
                                , 2L // 예: 좋아요 알림 유형 ID
                                , columnId,
                                notificationContent
                        )
                );
            }


            return ColumnLikeResponse.of(like, column.getLikeCount());
        } catch (Exception e) {
            log.error("칼럼 좋아요 추가 처리 중 오류 발생: columnId={}, userId={}, error={}", 
                    columnId, userId, e.getMessage(), e);
            throw new BusinessException(ErrorCode.LIKE_PROCESSING_ERROR);
        }
    }

    private boolean isFibonacci(int n) {
        int a = 0, b = 1;
        while (b < n) {
            int temp = b;
            b = a + b;
            a = temp;
        }
        return b == n;
    }


    private Like createColumnLike(Long columnId, Long userId) {
        return Like.builder()
                .columnId(columnId)
                .userId(userId)
                .isDelete(false)
                .build();
    }
} 