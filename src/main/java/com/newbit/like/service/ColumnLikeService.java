package com.newbit.like.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newbit.like.dto.response.ColumnLikeResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ColumnLikeService {

    private final LikeCommandService likeCommandService;
    private final LikeQueryService likeQueryService;

    @Transactional
    public ColumnLikeResponse toggleLike(Long columnId, Long userId) {
        return likeCommandService.toggleColumnLike(columnId, userId);
    }

    @Transactional(readOnly = true)
    public boolean isLiked(Long columnId, Long userId) {
        return likeQueryService.isColumnLiked(columnId, userId);
    }

    @Transactional(readOnly = true)
    public int getLikeCount(Long columnId) {
        return likeQueryService.getColumnLikeCount(columnId);
    }
} 