package com.newbit.newbitfeatureservice.like.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newbit.newbitfeatureservice.column.service.ColumnService;
import com.newbit.newbitfeatureservice.common.exception.BusinessException;
import com.newbit.newbitfeatureservice.common.exception.ErrorCode;
import com.newbit.newbitfeatureservice.like.dto.response.ColumnLikeResponse;
import com.newbit.newbitfeatureservice.like.dto.response.PostLikeResponse;
import com.newbit.newbitfeatureservice.like.entity.Like;
import com.newbit.newbitfeatureservice.like.repository.LikeRepository;
import com.newbit.newbitfeatureservice.notification.command.application.dto.request.NotificationSendRequest;
import com.newbit.newbitfeatureservice.notification.command.application.service.NotificationCommandService;
import com.newbit.newbitfeatureservice.post.service.PostService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LikeCommandService {

    private final LikeRepository likeRepository;
    private final PostService postService;
    private final ColumnService columnService;
    private final PointRewardService pointRewardService;
    private final NotificationCommandService notificationCommandService;

    @Transactional
    public PostLikeResponse togglePostLike(Long postId, Long userId) {
        try {
            checkPostExists(postId);
            
            Long authorId = postService.getWriterIdByPostId(postId);
            if (authorId != null && authorId.equals(userId)) {
                throw new BusinessException(ErrorCode.LIKE_SELF_NOT_ALLOWED);
            }
            
            Optional<Like> existingLike = findExistingPostLike(postId, userId);
            
            if (existingLike.isPresent()) {
                return unlikePost(existingLike.get(), postId);
            } else {
                return likePost(postId, userId);
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.LIKE_ERROR);
        }
    }
    
    @Transactional
    public ColumnLikeResponse toggleColumnLike(Long columnId, Long userId) {
        try {
            columnService.getColumn(columnId);
            
            if (columnService.isColumnAuthor(columnId, userId)) {
                throw new BusinessException(ErrorCode.LIKE_SELF_NOT_ALLOWED);
            }
            
            Optional<Like> existingLike = findExistingColumnLike(columnId, userId);
            
            if (existingLike.isPresent()) {
                return unlikeColumn(existingLike.get(), columnId);
            } else {
                return likeColumn(columnId, userId);
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.LIKE_ERROR);
        }
    }

    private void checkPostExists(Long postId) {
        postService.getReportCountByPostId(postId);
    }

    private Optional<Like> findExistingPostLike(Long postId, Long userId) {
        return likeRepository.findByPostIdAndUserIdAndIsDeleteFalse(postId, userId);
    }

    private PostLikeResponse unlikePost(Like like, Long postId) {
        try {
            like.markAsDeleted();
            likeRepository.save(like);
            
            postService.decreaseLikeCount(postId);
            
            int updatedLikeCount = likeRepository.countByPostIdAndIsDeleteFalse(postId);
            
            return PostLikeResponse.unliked(postId, like.getUserId(), updatedLikeCount);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.LIKE_ERROR);
        }
    }

    private PostLikeResponse likePost(Long postId, Long userId) {
        try {
            Like like = createPostLike(postId, userId);
            likeRepository.save(like);
            
            postService.increaseLikeCount(postId);
            
            Long authorId = postService.getWriterIdByPostId(postId);
            pointRewardService.givePointIfFirstLike(postId, userId, authorId);

            int updatedLikeCount = likeRepository.countByPostIdAndIsDeleteFalse(postId);
            
            if (isFibonacci(updatedLikeCount)) {
                String title = "게시글";
                try {
                    title = postService.getPostTitle(postId);
                } catch (Exception e) {
                }
                
                String notificationContent = String.format("'%s' 게시글이 좋아요를 받았습니다. (총 %d개)",
                        title, updatedLikeCount);

                notificationCommandService.sendNotification(
                        new NotificationSendRequest(
                                authorId,
                                2L, // 예: 좋아요 알림 유형 ID
                                postId,
                                notificationContent
                        )
                );
            }
            
            return PostLikeResponse.of(like, updatedLikeCount);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.LIKE_ERROR);
        }
    }

    private Like createPostLike(Long postId, Long userId) {
        return Like.builder()
                .postId(postId)
                .userId(userId)
                .isDelete(false)
                .build();
    }
    
    private Optional<Like> findExistingColumnLike(Long columnId, Long userId) {
        return likeRepository.findByColumnIdAndUserIdAndIsDeleteFalse(columnId, userId);
    }
    
    private ColumnLikeResponse unlikeColumn(Like like, Long columnId) {
        try {
            like.markAsDeleted();
            likeRepository.save(like);
            
            columnService.decreaseLikeCount(columnId);
            
            int updatedLikeCount = likeRepository.countByColumnIdAndIsDeleteFalse(columnId);
            
            return ColumnLikeResponse.unliked(columnId, like.getUserId(), updatedLikeCount);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.LIKE_ERROR);
        }
    }
    
    private ColumnLikeResponse likeColumn(Long columnId, Long userId) {
        try {
            Like like = createColumnLike(columnId, userId);
            likeRepository.save(like);
            
            columnService.increaseLikeCount(columnId);
            
            int updatedLikeCount = likeRepository.countByColumnIdAndIsDeleteFalse(columnId);

            if(isFibonacci(updatedLikeCount)) {
                String title = "칼럼";
                try {
                    title = columnService.getColumnTitle(columnId);
                } catch (Exception ignored) {
                }
                
                String notificationContent = String.format("'%s' 칼럼이 좋아요를 받았습니다. (총 %d개)",
                        title, updatedLikeCount);

                // 멘토 정보 조회 및 알림 발송
                try {
                    Long mentorId = columnService.getColumn(columnId).getMentorId();
                    if (mentorId != null) {
                        Long authorId = columnService.getUserIdByMentorId(mentorId);
                        if (authorId != null) {
                            notificationCommandService.sendNotification(
                                    new NotificationSendRequest(
                                            authorId,
                                            2L, // 예: 좋아요 알림 유형 ID
                                            columnId,
                                            notificationContent
                                    )
                            );
                        }
                    }
                } catch (Exception ignored) {
                }
            }

            return ColumnLikeResponse.of(like, updatedLikeCount);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.LIKE_ERROR);
        }
    }

    private boolean isFibonacci(int n) {
        if (n <= 0) return false;
        
        if (n == 1 || n == 2 || n == 3 || n == 5 || n == 8 || n == 13 || 
            n == 21 || n == 34 || n == 55 || n == 89 || n == 144) {
            return true;
        }
        
        if (n > 100) {
            return isPerfectSquare(5 * n * n + 4) || isPerfectSquare(5 * n * n - 4);
        }
        
        return false;
    }
    
    private boolean isPerfectSquare(int n) {
        int sqrt = (int) Math.sqrt(n);
        return sqrt * sqrt == n;
    }

    private Like createColumnLike(Long columnId, Long userId) {
        return Like.builder()
                .columnId(columnId)
                .userId(userId)
                .isDelete(false)
                .build();
    }
}