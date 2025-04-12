package com.newbit.like.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newbit.like.repository.LikeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LikeQueryService {
    
    private final LikeRepository likeRepository;
    
    @Transactional(readOnly = true)
    public boolean isPostLiked(Long postId, Long userId) {
        return likeRepository.existsByPostIdAndUserIdAndIsDeleteFalse(postId, userId);
    }
    
    @Transactional(readOnly = true)
    public int getPostLikeCount(Long postId) {
        return likeRepository.countByPostIdAndIsDeleteFalse(postId);
    }
    
    @Transactional(readOnly = true)
    public boolean isColumnLiked(Long columnId, Long userId) {
        return likeRepository.existsByColumnIdAndUserIdAndIsDeleteFalse(columnId, userId);
    }
    
    @Transactional(readOnly = true)
    public int getColumnLikeCount(Long columnId) {
        return likeRepository.countByColumnIdAndIsDeleteFalse(columnId);
    }
} 