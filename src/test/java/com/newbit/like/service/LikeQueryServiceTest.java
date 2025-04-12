package com.newbit.like.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.newbit.like.repository.LikeRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("LikeQueryService 테스트")
class LikeQueryServiceTest {

    @Mock
    private LikeRepository likeRepository;

    @InjectMocks
    private LikeQueryService likeQueryService;

    private final long postId = 1L;
    private final long userId = 2L;

    @Nested
    @DisplayName("isPostLiked Method")
    class IsPostLiked {

        @Test
        @DisplayName("좋아요 여부를 정확히 반환해야 한다")
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
    @DisplayName("getPostLikeCount Method")
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
} 