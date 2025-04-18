package com.newbit.newbitfeatureservice.like.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newbit.newbitfeatureservice.column.service.ColumnService;
import com.newbit.newbitfeatureservice.common.dto.Pagination;
import com.newbit.newbitfeatureservice.common.exception.BusinessException;
import com.newbit.newbitfeatureservice.common.exception.ErrorCode;
import com.newbit.newbitfeatureservice.like.dto.response.LikedColumnListResponse;
import com.newbit.newbitfeatureservice.like.dto.response.LikedColumnResponse;
import com.newbit.newbitfeatureservice.like.dto.response.LikedPostListResponse;
import com.newbit.newbitfeatureservice.like.dto.response.LikedPostResponse;
import com.newbit.newbitfeatureservice.like.entity.Like;
import com.newbit.newbitfeatureservice.like.repository.LikeRepository;
import com.newbit.newbitfeatureservice.post.service.PostService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeQueryService {
    
    private final LikeRepository likeRepository;
    private final PostService postService;
    private final ColumnService columnService;
    
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
                try {
                    Long postId = like.getPostId();
                    
                    postService.getReportCountByPostId(postId);
                    
                    String postTitle = postService.getPostTitle(postId);
                    Long authorId = postService.getWriterIdByPostId(postId);
                    
                    return LikedPostResponse.builder()
                        .likeId(like.getId())
                        .postId(postId)
                        .postTitle(postTitle)
                        .authorId(authorId)
                        .likedAt(like.getCreatedAt())
                        .build();
                } catch (Exception e) {
                    log.warn("좋아요한 게시글 정보 조회 실패: likeId={}, postId={}, 오류={}", like.getId(), like.getPostId(), e.getMessage());
                    return null;
                }
            })
            .filter(response -> response != null)
            .collect(Collectors.toList());
    }
    
    private List<LikedColumnResponse> mapToLikedColumnResponses(List<Like> likes) {
        return likes.stream()
            .map(like -> {
                try {
                    Long columnId = like.getColumnId();
                    
                    String columnTitle = columnService.getColumnTitle(columnId);
                    Long mentorId = columnService.getMentorIdByColumnId(columnId);
                    
                    if (mentorId == null) {
                        log.warn("칼럼의 멘토 정보가 없음: columnId={}", columnId);
                        return null;
                    }
                    
                    Long authorId = columnService.getUserIdByMentorId(mentorId);
                    
                    return LikedColumnResponse.builder()
                        .likeId(like.getId())
                        .columnId(columnId)
                        .columnTitle(columnTitle)
                        .authorId(authorId)
                        .likedAt(like.getCreatedAt())
                        .build();
                } catch (Exception e) {
                    log.warn("좋아요한 칼럼 정보 조회 실패: likeId={}, columnId={}, 오류={}", like.getId(), like.getColumnId(), e.getMessage());
                    return null;
                }
            })
            .filter(response -> response != null)
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