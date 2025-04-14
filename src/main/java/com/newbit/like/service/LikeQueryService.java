package com.newbit.like.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newbit.column.domain.Column;
import com.newbit.column.repository.ColumnRepository;
import com.newbit.common.dto.Pagination;
import com.newbit.common.exception.BusinessException;
import com.newbit.common.exception.ErrorCode;
import com.newbit.like.dto.response.LikedColumnListResponse;
import com.newbit.like.dto.response.LikedColumnResponse;
import com.newbit.like.dto.response.LikedPostListResponse;
import com.newbit.like.dto.response.LikedPostResponse;
import com.newbit.like.entity.Like;
import com.newbit.like.repository.LikeRepository;
import com.newbit.post.entity.Post;
import com.newbit.post.repository.PostRepository;
import com.newbit.user.entity.User;
import com.newbit.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeQueryService {
    
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final ColumnRepository columnRepository;
    private final UserRepository userRepository;
    
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
    
    @Transactional(readOnly = true)
    public LikedPostListResponse getLikedPostsByUser(Long userId, Pageable pageable) {
        try {
            Page<Like> likedPosts = likeRepository.findLikedPostsByUserId(userId, pageable);
            List<LikedPostResponse> postResponses = mapToLikedPostResponses(likedPosts.getContent());
            Pagination pagination = createPagination(pageable, likedPosts);
            
            return LikedPostListResponse.builder()
                .likedPosts(postResponses)
                .pagination(pagination)
                .build();
        } catch (BusinessException e) {
            log.error("비즈니스 예외 발생: userId={}, errorCode={}, message={}", 
                    userId, e.getErrorCode(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("좋아요한 게시글 조회 중 예기치 않은 오류 발생: userId={}, error={}", 
                    userId, e.getMessage(), e);
            throw new BusinessException(ErrorCode.LIKE_ERROR);
        }
    }
    
    @Transactional(readOnly = true)
    public LikedColumnListResponse getLikedColumnsByUser(Long userId, Pageable pageable) {
        try {
            Page<Like> likedColumns = likeRepository.findLikedColumnsByUserId(userId, pageable);
            List<LikedColumnResponse> columnResponses = mapToLikedColumnResponses(likedColumns.getContent());
            Pagination pagination = createPagination(pageable, likedColumns);
            
            return LikedColumnListResponse.builder()
                .likedColumns(columnResponses)
                .pagination(pagination)
                .build();
        } catch (BusinessException e) {
            log.error("비즈니스 예외 발생: userId={}, errorCode={}, message={}", 
                    userId, e.getErrorCode(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("좋아요한 칼럼 조회 중 예기치 않은 오류 발생: userId={}, error={}", 
                    userId, e.getMessage(), e);
            throw new BusinessException(ErrorCode.LIKE_ERROR);
        }
    }
    
    private List<LikedPostResponse> mapToLikedPostResponses(List<Like> likes) {
        return likes.stream()
            .map(like -> {
                Post post = postRepository.findByIdAndDeletedAtIsNull(like.getPostId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
                    
                String authorNickname = userRepository.findById(post.getUserId())
                    .map(User::getNickname)
                    .orElse("알 수 없음");
                    
                return LikedPostResponse.builder()
                    .likeId(like.getId())
                    .postId(post.getId())
                    .postTitle(post.getTitle())
                    .authorId(post.getUserId())
                    .authorNickname(authorNickname)
                    .likedAt(like.getCreatedAt())
                    .build();
            })
            .collect(Collectors.toList());
    }
    
    private List<LikedColumnResponse> mapToLikedColumnResponses(List<Like> likes) {
        return likes.stream()
            .map(like -> {
                Column column = columnRepository.findById(like.getColumnId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.COLUMN_NOT_FOUND));
                
                Long authorId = column.getMentor().getUser().getUserId();
                String authorNickname = userRepository.findById(authorId)
                    .map(User::getNickname)
                    .orElse("알 수 없음");
                    
                return LikedColumnResponse.builder()
                    .likeId(like.getId())
                    .columnId(column.getColumnId())
                    .columnTitle(column.getTitle())
                    .authorId(authorId)
                    .authorNickname(authorNickname)
                    .likedAt(like.getCreatedAt())
                    .build();
            })
            .collect(Collectors.toList());
    }
    
    private Pagination createPagination(Pageable pageable, Page<?> page) {
        return Pagination.builder()
            .currentPage(pageable.getPageNumber() + 1)
            .totalPage(page.getTotalPages())
            .totalItems(page.getTotalElements())
            .build();
    }
} 