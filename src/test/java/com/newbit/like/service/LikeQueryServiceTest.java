package com.newbit.like.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

import com.newbit.column.domain.Column;
import com.newbit.column.repository.ColumnRepository;
import com.newbit.common.dto.Pagination;
import com.newbit.like.dto.response.LikedColumnListResponse;
import com.newbit.like.dto.response.LikedColumnResponse;
import com.newbit.like.dto.response.LikedPostListResponse;
import com.newbit.like.dto.response.LikedPostResponse;
import com.newbit.like.entity.Like;
import com.newbit.like.repository.LikeRepository;
import com.newbit.post.entity.Post;
import com.newbit.post.repository.PostRepository;
import com.newbit.user.entity.Mentor;
import com.newbit.user.entity.User;
import com.newbit.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("LikeQueryService 테스트")
class LikeQueryServiceTest {

    @Mock
    private LikeRepository likeRepository;
    
    @Mock
    private PostRepository postRepository;
    
    @Mock
    private ColumnRepository columnRepository;
    
    @Mock
    private UserRepository userRepository;

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
            
            Post post1 = createPost(postId, "제목1", userId);
            Post post2 = createPost(postId + 1, "제목2", userId);
            
            User user = createUser(userId, "사용자");
            
            when(likeRepository.findLikedPostsByUserId(userId, pageable)).thenReturn(likePage);
            when(postRepository.findByIdAndDeletedAtIsNull(postId)).thenReturn(Optional.of(post1));
            when(postRepository.findByIdAndDeletedAtIsNull(postId + 1)).thenReturn(Optional.of(post2));
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            
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
            assertEquals("사용자", firstPost.getAuthorNickname());
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
            
            Column column1 = createColumn(columnId, "칼럼1", userId);
            Column column2 = createColumn(columnId + 1, "칼럼2", userId);
            
            User user = createUser(userId, "칼럼작성자");
            
            when(likeRepository.findLikedColumnsByUserId(userId, pageable)).thenReturn(likePage);
            when(columnRepository.findById(columnId)).thenReturn(Optional.of(column1));
            when(columnRepository.findById(columnId + 1)).thenReturn(Optional.of(column2));
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            
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
            assertEquals("칼럼작성자", firstColumn.getAuthorNickname());
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
    
    private Post createPost(Long postId, String title, Long userId) {
        Post post = mock(Post.class);
        when(post.getId()).thenReturn(postId);
        when(post.getTitle()).thenReturn(title);
        when(post.getUserId()).thenReturn(userId);
        return post;
    }
    
    private Column createColumn(Long columnId, String title, Long authorId) {
        Column column = mock(Column.class);
        Mentor mentor = mock(Mentor.class);
        User user = mock(User.class);
        
        when(column.getColumnId()).thenReturn(columnId);
        when(column.getTitle()).thenReturn(title);
        when(column.getMentor()).thenReturn(mentor);
        when(mentor.getUser()).thenReturn(user);
        when(user.getUserId()).thenReturn(authorId);
        
        return column;
    }
    
    private User createUser(Long userId, String nickname) {
        User user = mock(User.class);
        
        // 이 유저 객체는 테스트에서 실제로 사용되므로 필요한 스텁만 설정
        when(user.getNickname()).thenReturn(nickname);
        
        return user;
    }
} 