package com.newbit.newbitfeatureservice.like.service;

import com.newbit.newbitfeatureservice.column.service.ColumnService;
import com.newbit.newbitfeatureservice.common.dto.Pagination;
import com.newbit.newbitfeatureservice.like.dto.response.LikedColumnListResponse;
import com.newbit.newbitfeatureservice.like.dto.response.LikedColumnResponse;
import com.newbit.newbitfeatureservice.like.dto.response.LikedPostListResponse;
import com.newbit.newbitfeatureservice.like.dto.response.LikedPostResponse;
import com.newbit.newbitfeatureservice.like.entity.Like;
import com.newbit.newbitfeatureservice.like.repository.LikeRepository;
import com.newbit.newbitfeatureservice.post.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("LikeQueryService 테스트")
class LikeQueryServiceTest {

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private ColumnService columnService;
    
    @Mock
    private PostService postService;

    @InjectMocks
    private LikeQueryService likeQueryService;

    private final long postId = 1L;
    private final long userId = 2L;
    private final long columnId = 3L;

    @Nested
    @DisplayName("isPostLiked 메서드")
    class IsPostLiked {

        @Test
        @DisplayName("게시글 좋아요 여부를 정확히 반환해야 한다")
        void shouldReturnCorrectLikeStatus() {
            // Given
            when(likeRepository.existsByPostIdAndUserIdAndIsDeleteFalse(postId, userId)).thenReturn(true);

            // When
            boolean result = likeQueryService.isPostLiked(postId, userId);

            // Then
            assertTrue(result);
        }
    }

    @Nested
    @DisplayName("getPostLikeCount 메서드")
    class GetPostLikeCount {

        @Test
        @DisplayName("게시글의 좋아요 수를 정확히 반환해야 한다")
        void shouldReturnCorrectLikeCount() {
            // Given
            when(likeRepository.countByPostIdAndIsDeleteFalse(postId)).thenReturn(10);

            // When
            int count = likeQueryService.getPostLikeCount(postId);

            // Then
            assertEquals(10, count);
        }
    }
    
    @Nested
    @DisplayName("isColumnLiked 메서드")
    class IsColumnLiked {

        @Test
        @DisplayName("칼럼 좋아요 여부를 정확히 반환해야 한다")
        void shouldReturnCorrectLikeStatus() {
            // Given
            when(likeRepository.existsByColumnIdAndUserIdAndIsDeleteFalse(columnId, userId)).thenReturn(true);

            // When
            boolean result = likeQueryService.isColumnLiked(columnId, userId);

            // Then
            assertTrue(result);
        }
    }

    @Nested
    @DisplayName("getColumnLikeCount 메서드")
    class GetColumnLikeCount {

        @Test
        @DisplayName("칼럼의 좋아요 수를 정확히 반환해야 한다")
        void shouldReturnCorrectLikeCount() {
            // Given
            when(likeRepository.countByColumnIdAndIsDeleteFalse(columnId)).thenReturn(15);

            // When
            int count = likeQueryService.getColumnLikeCount(columnId);

            // Then
            assertEquals(15, count);
        }
    }
    
    @Nested
    @DisplayName("getLikedPostsByUser 메서드")
    class GetLikedPostsByUser {
        
        @Test
        @DisplayName("사용자가 좋아요한 게시글 목록을 페이지네이션하여 정확히 반환해야 한다")
        void shouldReturnPaginatedLikedPosts() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);
            LocalDateTime now = LocalDateTime.now();
            
            Like like1 = createPostLike(1L, postId, userId, now);
            Like like2 = createPostLike(2L, postId + 1, userId, now.minusDays(1));
            List<Like> likes = List.of(like1, like2);
            
            Page<Like> likePage = new PageImpl<>(likes, pageable, likes.size());
            
            // PostService 스터빙 추가
            when(likeRepository.findLikedPostsByUserId(userId, pageable)).thenReturn(likePage);
            when(postService.getReportCountByPostId(postId)).thenReturn(0);
            when(postService.getReportCountByPostId(postId + 1)).thenReturn(0);
            when(postService.getPostTitle(postId)).thenReturn("제목1");
            when(postService.getPostTitle(postId + 1)).thenReturn("제목2");
            when(postService.getWriterIdByPostId(postId)).thenReturn(userId);
            when(postService.getWriterIdByPostId(postId + 1)).thenReturn(userId);
            
            // When
            LikedPostListResponse response = likeQueryService.getLikedPostsByUser(userId, pageable);
            
            // Then
            assertNotNull(response);
            assertEquals(2, response.getLikedPosts().size());
            
            Pagination pagination = response.getPagination();
            assertNotNull(pagination);
            assertEquals(1, pagination.getCurrentPage());
            assertEquals(1, pagination.getTotalPage());
            assertEquals(2, pagination.getTotalItems());
            
            LikedPostResponse firstPost = response.getLikedPosts().get(0);
            assertEquals(1L, firstPost.getLikeId());
            assertEquals(postId, firstPost.getPostId());
            assertEquals("제목1", firstPost.getPostTitle());
            assertEquals(userId, firstPost.getAuthorId());
            assertEquals(now, firstPost.getLikedAt());
            
            LikedPostResponse secondPost = response.getLikedPosts().get(1);
            assertEquals(2L, secondPost.getLikeId());
            assertEquals(postId + 1, secondPost.getPostId());
            assertEquals("제목2", secondPost.getPostTitle());
        }
    }
    
    @Nested
    @DisplayName("getLikedColumnsByUser 메서드")
    class GetLikedColumnsByUser {
        
        @Test
        @DisplayName("사용자가 좋아요한 칼럼 목록을 페이지네이션하여 정확히 반환해야 한다")
        void shouldReturnPaginatedLikedColumns() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);
            LocalDateTime now = LocalDateTime.now();
            
            Like like1 = createColumnLike(1L, columnId, userId, now);
            Like like2 = createColumnLike(2L, columnId + 1, userId, now.minusDays(1));
            List<Like> likes = List.of(like1, like2);
            
            Page<Like> likePage = new PageImpl<>(likes, pageable, likes.size());
            
            // ColumnService 스터빙 추가
            when(likeRepository.findLikedColumnsByUserId(userId, pageable)).thenReturn(likePage);
            when(columnService.getColumnTitle(columnId)).thenReturn("칼럼1");
            when(columnService.getColumnTitle(columnId + 1)).thenReturn("칼럼2");
            when(columnService.getMentorIdByColumnId(columnId)).thenReturn(1L);
            when(columnService.getMentorIdByColumnId(columnId + 1)).thenReturn(1L);
            when(columnService.getUserIdByMentorId(1L)).thenReturn(userId);
            
            // When
            LikedColumnListResponse response = likeQueryService.getLikedColumnsByUser(userId, pageable);
            
            // Then
            assertNotNull(response);
            assertEquals(2, response.getLikedColumns().size());
            
            Pagination pagination = response.getPagination();
            assertNotNull(pagination);
            assertEquals(1, pagination.getCurrentPage());
            assertEquals(1, pagination.getTotalPage());
            assertEquals(2, pagination.getTotalItems());
            
            LikedColumnResponse firstColumn = response.getLikedColumns().get(0);
            assertEquals(1L, firstColumn.getLikeId());
            assertEquals(columnId, firstColumn.getColumnId());
            assertEquals("칼럼1", firstColumn.getColumnTitle());
            assertEquals(userId, firstColumn.getAuthorId());
            assertEquals(now, firstColumn.getLikedAt());
            
            LikedColumnResponse secondColumn = response.getLikedColumns().get(1);
            assertEquals(2L, secondColumn.getLikeId());
            assertEquals(columnId + 1, secondColumn.getColumnId());
            assertEquals("칼럼2", secondColumn.getColumnTitle());
        }
    }
    
    private Like createPostLike(Long id, Long postId, Long userId, LocalDateTime createdAt) {
        Like like = Like.builder()
                .id(id)
                .postId(postId)
                .userId(userId)
                .isDelete(false)
                .build();
        ReflectionTestUtils.setField(like, "createdAt", createdAt);
        return like;
    }
    
    private Like createColumnLike(Long id, Long columnId, Long userId, LocalDateTime createdAt) {
        Like like = Like.builder()
                .id(id)
                .columnId(columnId)
                .userId(userId)
                .isDelete(false)
                .build();
        ReflectionTestUtils.setField(like, "createdAt", createdAt);
        return like;
    }
} 