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
import com.newbit.like.dto.response.ColumnLikeResponse;

@ExtendWith(MockitoExtension.class)
@DisplayName("ColumnLikeService 테스트")
class ColumnLikeServiceTest {
    @Mock
    private LikeQueryService likeQueryService;
    
    @Mock
    private LikeCommandService likeCommandService;

    @InjectMocks
    private ColumnLikeService columnLikeService;

    // 필요한 상수만 남기고 final로 선언
    private final long columnId = 1L;
    private final long userId = 2L;

    @BeforeEach
    void setUp() {
        // 필요한 경우 초기화 코드 추가
    }

    @Nested
    @DisplayName("toggleLike Method")
    class ToggleLike {

        @Nested
        @DisplayName("좋아요 추가 시")
        class AddLike {

            @Test
            @DisplayName("좋아요를 추가하고 응답을 반환해야 한다")
            void shouldAddLikeAndReturnResponse() {
                // Given
                ColumnLikeResponse expectedResponse = ColumnLikeResponse.builder()
                    .columnId(columnId)
                    .userId(userId)
                    .liked(true)
                    .totalLikes(11)
                    .build();
                
                when(likeCommandService.toggleColumnLike(columnId, userId)).thenReturn(expectedResponse);

                // When
                ColumnLikeResponse response = columnLikeService.toggleLike(columnId, userId);

                // Then
                assertNotNull(response);
                assertTrue(response.isLiked());
                assertEquals(11, response.getTotalLikes());
                
                verify(likeCommandService).toggleColumnLike(columnId, userId);
            }
        }

        @Nested
        @DisplayName("좋아요 취소 시")
        class CancelLike {

            @Test
            @DisplayName("좋아요 수를 감소시키고 응답을 반환해야 한다")
            void shouldDecreaseLikeCountAndReturnResponse() {
                // Given
                ColumnLikeResponse expectedResponse = ColumnLikeResponse.builder()
                    .columnId(columnId)
                    .userId(userId)
                    .liked(false)
                    .totalLikes(9)
                    .build();
                
                when(likeCommandService.toggleColumnLike(columnId, userId)).thenReturn(expectedResponse);

                // When
                ColumnLikeResponse response = columnLikeService.toggleLike(columnId, userId);

                // Then
                assertNotNull(response);
                assertFalse(response.isLiked());
                assertEquals(9, response.getTotalLikes());
                
                verify(likeCommandService).toggleColumnLike(columnId, userId);
            }
        }

        @Nested
        @DisplayName("예외 처리")
        class HandleExceptions {

            @Test
            @DisplayName("칼럼이 없으면 예외를 발생시켜야 한다")
            void shouldThrowExceptionWhenColumnNotFound() {
                // Given
                when(likeCommandService.toggleColumnLike(columnId, userId)).thenThrow(new BusinessException(ErrorCode.COLUMN_NOT_FOUND));

                // When & Then
                assertThrows(BusinessException.class, () -> columnLikeService.toggleLike(columnId, userId));
                verify(likeCommandService).toggleColumnLike(columnId, userId);
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
            when(likeQueryService.isColumnLiked(columnId, userId)).thenReturn(true);

            // When
            boolean result = columnLikeService.isLiked(columnId, userId);

            // Then
            assertTrue(result);
            verify(likeQueryService).isColumnLiked(columnId, userId);
        }
    }

    @Nested
    @DisplayName("getLikeCount Method")
    class GetLikeCount {

        @Test
        @DisplayName("칼럼의 좋아요 수를 정확히 반환해야 한다")
        void shouldReturnCorrectLikeCount() {
            // Given
            when(likeQueryService.getColumnLikeCount(columnId)).thenReturn(10);

            // When
            int count = columnLikeService.getLikeCount(columnId);

            // Then
            assertEquals(10, count);
            verify(likeQueryService).getColumnLikeCount(columnId);
        }
    }
} 