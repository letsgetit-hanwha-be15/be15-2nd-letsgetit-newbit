package com.newbit.newbitfeatureservice.like.service;

import com.newbit.newbitfeatureservice.purchase.command.domain.PointTypeConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newbit.newbitfeatureservice.common.exception.BusinessException;
import com.newbit.newbitfeatureservice.like.repository.LikeRepository;
import com.newbit.newbitfeatureservice.purchase.command.application.service.PointTransactionCommandService;

import lombok.RequiredArgsConstructor;

@Slf4j
@Service
@RequiredArgsConstructor
public class PointRewardService {
    
    private final LikeRepository likeRepository;
    private final PointTransactionCommandService pointTransactionCommandService;

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
            log.error("비즈니스 예외 발생: userId={}, errorCode={}, message={}",
                    authorId, e.getErrorCode(), e.getMessage());
        } catch (Exception e) {
            log.error("비즈니스 범용 예외 발생: userId={}, errorClass={}, message={}",
                    authorId, e.getClass(), e.getMessage());
        }
    }
} 