package com.newbit.like.service;

import com.newbit.purchase.command.domain.PointTypeConstants;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newbit.common.exception.BusinessException;
import com.newbit.like.repository.LikeRepository;
import com.newbit.purchase.command.application.service.PointTransactionCommandService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PointRewardService {
    
    private final LikeRepository likeRepository;
    private final PointTransactionCommandService pointTransactionCommandService;

    @Transactional
    public void givePointIfFirstLike(Long postId, Long userId, Long authorId) {
        if (isFirstLike(postId, userId)) {
            givePointToAuthor(authorId, postId);
        }
    }
    
    private boolean isFirstLike(Long postId, Long userId) {
        return likeRepository.countByPostIdAndUserId(postId, userId) == 1;
    }
    
    private void givePointToAuthor(Long authorId, Long postId) {
        try {
            pointTransactionCommandService.givePointByType(authorId, PointTypeConstants.LIKES, postId);
            log.info("게시글 좋아요 포인트 지급 성공: authorId={}, postId={}", authorId, postId);
        } catch (BusinessException e) {
            log.error("포인트 지급 중 비즈니스 예외 발생: authorId={}, postId={}, errorCode={}, message={}", 
                    authorId, postId, e.getErrorCode(), e.getMessage());
        } catch (Exception e) {
            log.error("포인트 지급 중 예기치 않은 오류 발생: authorId={}, postId={}, error={}", 
                    authorId, postId, e.getMessage(), e);
        }
    }
} 