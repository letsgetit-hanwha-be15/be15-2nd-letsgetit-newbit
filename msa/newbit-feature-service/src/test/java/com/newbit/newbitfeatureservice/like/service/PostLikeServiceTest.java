package com.newbit.newbitfeatureservice.like.service;

import com.newbit.newbitfeatureservice.common.exception.BusinessException;
import com.newbit.newbitfeatureservice.common.exception.ErrorCode;
import com.newbit.newbitfeatureservice.like.dto.response.LikedPostListResponse;
import com.newbit.newbitfeatureservice.like.dto.response.PostLikeResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PostLikeService 테스트")
class PostLikeServiceTest {
    @Mock
    private LikeQueryService likeQueryService;
    
    @Mock
    private LikeCommandService likeCommandService;

    @InjectMocks
    private PostLikeService postLikeService;

    private final long postId = 1L;
    private final long userId = 2L;

    @BeforeEach
    void setUp() {
    }

    @Nested
    @DisplayName("toggleLike 메서드")
    class ToggleLike {

        @Nested
        @DisplayName("좋아요 추가 시")
        class AddLike {

            @Test
            @DisplayName("좋아요를 추가하고 응답을 반환해야 한다")
            void shouldAddLikeAndReturnResponse() {
                // Given
                PostLikeResponse expectedResponse = PostLikeResponse.builder()
                    .postId(postId)
                    .userId(userId)
                    .liked(true)
                    .totalLikes(11)
                    .build();
                
                when(likeCommandService.togglePostLike(postId, userId)).thenReturn(expectedResponse);

                // When
                PostLikeResponse response = postLikeService.toggleLike(postId, userId);

                // Then
                assertNotNull(response);
                assertTrue(response.isLiked());
                assertEquals(11, response.getTotalLikes());
                
                verify(likeCommandService).togglePostLike(postId, userId);
            }
        }

        @Nested
        @DisplayName("좋아요 취소 시")
        class CancelLike {

            @Test
            @DisplayName("좋아요 수를 감소시키고 소프트 딜리트해야 한다")
            void shouldDecreaseLikeCountAndSoftDelete() {
                // Given
                PostLikeResponse expectedResponse = PostLikeResponse.builder()
                    .postId(postId)
                    .userId(userId)
                    .liked(false)
                    .totalLikes(9)
                    .build();
                
                when(likeCommandService.togglePostLike(postId, userId)).thenReturn(expectedResponse);

                // When
                PostLikeResponse response = postLikeService.toggleLike(postId, userId);

                // Then
                assertNotNull(response);
                assertFalse(response.isLiked());
                assertEquals(9, response.getTotalLikes());
                
                verify(likeCommandService).togglePostLike(postId, userId);
            }
        }

        @Nested
        @DisplayName("예외 처리")
        class HandleExceptions {

            @Test
            @DisplayName("게시글이 없으면 예외를 발생시켜야 한다")
            void shouldThrowExceptionWhenPostNotFound() {
                // Given
                when(likeCommandService.togglePostLike(postId, userId)).thenThrow(new BusinessException(ErrorCode.POST_NOT_FOUND));

                // When & Then
                assertThrows(BusinessException.class, () -> postLikeService.toggleLike(postId, userId));
                verify(likeCommandService).togglePostLike(postId, userId);
            }
        }
    }

    @Nested
    @DisplayName("isLiked 메서드")
    class IsLiked {

        @Test
        @DisplayName("좋아요 여부를 정확히 반환해야 한다")
        void shouldReturnCorrectLikeStatus() {
            // Given
            when(likeQueryService.isPostLiked(postId, userId)).thenReturn(true);

            // When
            boolean result = postLikeService.isLiked(postId, userId);

            // Then
            assertTrue(result);
            verify(likeQueryService).isPostLiked(postId, userId);
        }
    }

    @Nested
    @DisplayName("getLikeCount 메서드")
    class GetLikeCount {

        @Test
        @DisplayName("게시글의 좋아요 수를 정확히 반환해야 한다")
        void shouldReturnCorrectLikeCount() {
            // Given
            when(likeQueryService.getPostLikeCount(postId)).thenReturn(10);

            // When
            int count = postLikeService.getLikeCount(postId);

            // Then
            assertEquals(10, count);
            verify(likeQueryService).getPostLikeCount(postId);
        }
    }
    
    @Nested
    @DisplayName("getLikedPostsByUser 메서드")
    class GetLikedPostsByUser {

        @Test
        @DisplayName("사용자가 좋아요한 게시글 목록을 조회하고 결과를 반환해야 한다")
        void shouldReturnLikedPostsByUser() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);
            LikedPostListResponse expectedResponse = LikedPostListResponse.builder().build();
            
            when(likeQueryService.getLikedPostsByUser(userId, pageable)).thenReturn(expectedResponse);

            // When
            LikedPostListResponse response = postLikeService.getLikedPostsByUser(userId, pageable);

            // Then
            assertNotNull(response);
            assertEquals(expectedResponse, response);
            verify(likeQueryService).getLikedPostsByUser(userId, pageable);
        }
    }
} 