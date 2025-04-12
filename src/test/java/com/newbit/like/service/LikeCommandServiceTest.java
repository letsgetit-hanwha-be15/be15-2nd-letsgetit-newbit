package com.newbit.like.service;

import java.util.Optional;

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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.newbit.common.exception.BusinessException;
import com.newbit.common.exception.ErrorCode;
import com.newbit.like.dto.response.PostLikeResponse;
import com.newbit.like.entity.Like;
import com.newbit.like.repository.LikeRepository;
import com.newbit.post.entity.Post;
import com.newbit.post.repository.PostRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("LikeCommandService 테스트")
class LikeCommandServiceTest {

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private PointRewardService pointRewardService;

    @InjectMocks
    private LikeCommandService likeCommandService;

    private Post post;
    private Like like;
    private final long postId = 1L;
    private final long userId = 2L;
    private final long authorId = 3L;

    @BeforeEach
    void setUp() {
        post = mock(Post.class);
        
        like = Like.builder()
                .id(1L)
                .postId(postId)
                .userId(userId)
                .isDelete(false)
                .build();
    }

    @Nested
    @DisplayName("togglePostLike Method")
    class TogglePostLike {

        @Nested
        @DisplayName("좋아요 추가 시")
        class AddLike {

            @Test
            @DisplayName("좋아요를 추가하고 포인트 지급 처리를 호출해야 한다")
            void shouldAddLikeAndCallPointReward() {
                // Given
                when(post.getUserId()).thenReturn(authorId);
                when(post.getLikeCount()).thenReturn(10).thenReturn(11);
                when(postRepository.findByIdAndDeletedAtIsNull(postId)).thenReturn(Optional.of(post));
                when(likeRepository.findByPostIdAndUserIdAndIsDeleteFalse(postId, userId)).thenReturn(Optional.empty());
                when(likeRepository.save(any(Like.class))).thenReturn(like);

                // When
                PostLikeResponse response = likeCommandService.togglePostLike(postId, userId);

                // Then
                assertNotNull(response);
                assertTrue(response.isLiked());
                assertEquals(11, response.getTotalLikes());

                verify(post).setLikeCount(11);
                verify(postRepository).save(post);
                verify(pointRewardService).givePointIfFirstLike(postId, userId, authorId);
            }
        }

        @Nested
        @DisplayName("좋아요 취소 시")
        class CancelLike {

            @Test
            @DisplayName("좋아요 수를 감소시키고 소프트 딜리트해야 한다")
            void shouldDecreaseLikeCountAndSoftDelete() {
                // Given
                when(post.getLikeCount()).thenReturn(10).thenReturn(9);
                when(postRepository.findByIdAndDeletedAtIsNull(postId)).thenReturn(Optional.of(post));
                when(likeRepository.findByPostIdAndUserIdAndIsDeleteFalse(postId, userId)).thenReturn(Optional.of(like));

                // When
                PostLikeResponse response = likeCommandService.togglePostLike(postId, userId);

                // Then
                assertNotNull(response);
                assertFalse(response.isLiked());
                assertEquals(9, response.getTotalLikes());

                verify(likeRepository).save(like);
                verify(post).setLikeCount(9);
                verify(postRepository).save(post);
                verify(pointRewardService, never()).givePointIfFirstLike(anyLong(), anyLong(), anyLong());
            }
        }

        @Nested
        @DisplayName("예외 처리")
        class HandleExceptions {

            @Test
            @DisplayName("게시글이 없으면 POST_LIKE_NOT_FOUND 예외를 발생시켜야 한다")
            void shouldThrowExceptionWhenPostNotFound() {
                // Given
                when(postRepository.findByIdAndDeletedAtIsNull(postId)).thenReturn(Optional.empty());

                // When & Then
                BusinessException exception = assertThrows(BusinessException.class, () -> 
                    likeCommandService.togglePostLike(postId, userId));
                
                assertEquals(ErrorCode.POST_LIKE_NOT_FOUND, exception.getErrorCode());
                verify(likeRepository, never()).save(any(Like.class));
                verify(pointRewardService, never()).givePointIfFirstLike(anyLong(), anyLong(), anyLong());
            }
        }
    }
} 