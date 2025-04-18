package com.newbit.like.service;

import com.newbit.purchase.command.domain.PointTypeConstants;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newbit.common.exception.BusinessException;
import com.newbit.like.repository.LikeRepository;
import com.newbit.purchase.command.application.service.PointTransactionCommandService;

import lombok.RequiredArgsConstructor;

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
        } catch (BusinessException e) {
            // 비즈니스 예외 처리
        } catch (Exception e) {
            // 예기치 않은 오류 처리
        }
    }
} 