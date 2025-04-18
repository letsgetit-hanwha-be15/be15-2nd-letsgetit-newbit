package com.newbit.newbitfeatureservice.like.service;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newbit.newbitfeatureservice.like.dto.response.LikedPostListResponse;
import com.newbit.newbitfeatureservice.like.dto.response.PostLikeResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostLikeService {

    private final LikeCommandService likeCommandService;
    private final LikeQueryService likeQueryService;

    @Transactional
    public PostLikeResponse toggleLike(Long postId, Long userId) {
        return likeCommandService.togglePostLike(postId, userId);
    }

    @Transactional(readOnly = true)
    public boolean isLiked(Long postId, Long userId) {
        return likeQueryService.isPostLiked(postId, userId);
    }

    @Transactional(readOnly = true)
    public int getLikeCount(Long postId) {
        return likeQueryService.getPostLikeCount(postId);
    }
    
    @Transactional(readOnly = true)
    public LikedPostListResponse getLikedPostsByUser(Long userId, Pageable pageable) {
        return likeQueryService.getLikedPostsByUser(userId, pageable);
    }
} 