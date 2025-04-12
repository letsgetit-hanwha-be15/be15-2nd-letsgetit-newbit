package com.newbit.like.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newbit.common.exception.BusinessException;
import com.newbit.common.exception.ErrorCode;
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
    private final PointRewardService pointRewardService;

    @Transactional
    public PostLikeResponse togglePostLike(Long postId, Long userId) {
        try {
            Post post = findPostById(postId);
            Optional<Like> existingLike = findExistingLike(postId, userId);
            
            if (existingLike.isPresent()) {
                return unlikePost(existingLike.get(), post);
            } else {
                return likePost(postId, userId, post);
            }
        } catch (BusinessException e) {
            // 이미 정의된 비즈니스 예외는 그대로 던짐
            log.error("좋아요 처리 중 비즈니스 예외 발생: postId={}, userId={}, errorCode={}, message={}",
                    postId, userId, e.getErrorCode(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("좋아요 처리 중 예기치 않은 오류 발생: postId={}, userId={}, error={}", 
                    postId, userId, e.getMessage(), e);
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

    private Optional<Like> findExistingLike(Long postId, Long userId) {
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
            Like like = createLike(postId, userId);
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

    private Like createLike(Long postId, Long userId) {
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
} 