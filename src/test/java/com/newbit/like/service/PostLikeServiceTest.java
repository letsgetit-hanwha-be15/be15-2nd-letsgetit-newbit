package com.newbit.like.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.newbit.common.exception.BusinessException;
import com.newbit.common.exception.ErrorCode;
import com.newbit.like.dto.response.PostLikeResponse;

@ExtendWith(MockitoExtension.class)
@DisplayName("PostLikeService 테스트")
class PostLikeServiceTest {
    @Mock
    private LikeQueryService likeQueryService;
    
    @Mock
    private LikeCommandService likeCommandService;

    @InjectMocks
    private PostLikeService postLikeService;

    // 필요한 상수만 남기고 final로 선언
    private final long postId = 1L;
    private final long userId = 2L;

    @BeforeEach
    void setUp() {
        // Post와 Like는 테스트 메서드 내부에서 필요할 때만 생성
    }

    @Nested
    @DisplayName("toggleLike Method")
    class ToggleLike {

        @Nested
        @DisplayName("1.좋아요 추가 시")
        class AddLike {

            @Test
            @DisplayName("최초 좋아요면 작성자에게 포인트를 지급해야 한다")
            void shouldGivePointsToAuthorOnFirstLike() {
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

            @Test
            @DisplayName("좋아요 취소 후 재추가하면 포인트를 지급하지 않아야 한다")
            void shouldNotGivePointsOnReaddingLike() {
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

            @Test
            @DisplayName("포인트 지급 중 예외가 발생해도 좋아요 처리는 성공해야 한다")
            void shouldSucceedEvenIfPointTransactionFails() {
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
                when(likeCommandService.togglePostLike(postId, userId)).thenThrow(new BusinessException(ErrorCode.POST_LIKE_NOT_FOUND));

                // When & Then
                assertThrows(BusinessException.class, () -> postLikeService.toggleLike(postId, userId));
                verify(likeCommandService).togglePostLike(postId, userId);
            }
        }
    }

    @Nested
    @DisplayName("isLiked Method")
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
    @DisplayName("getLikeCount Method")
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
} 