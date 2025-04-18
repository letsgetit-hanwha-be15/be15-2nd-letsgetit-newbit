package com.newbit.newbitfeatureservice.like.service;

import com.newbit.newbitfeatureservice.column.domain.Column;
import com.newbit.newbitfeatureservice.column.service.ColumnService;
import com.newbit.newbitfeatureservice.common.exception.BusinessException;
import com.newbit.newbitfeatureservice.common.exception.ErrorCode;
import com.newbit.newbitfeatureservice.like.dto.response.ColumnLikeResponse;
import com.newbit.newbitfeatureservice.like.dto.response.PostLikeResponse;
import com.newbit.newbitfeatureservice.like.entity.Like;
import com.newbit.newbitfeatureservice.like.repository.LikeRepository;
import com.newbit.newbitfeatureservice.notification.command.application.service.NotificationCommandService;
import com.newbit.newbitfeatureservice.post.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LikeCommandService 테스트")
class LikeCommandServiceTest {

    @Mock
    private LikeRepository likeRepository;
    
    @Mock
    private PointRewardService pointRewardService;
    
    @Mock
    private PostService postService;
    
    @Mock
    private ColumnService columnService;
    
    @Mock
    private NotificationCommandService notificationCommandService;

    @InjectMocks
    private LikeCommandService likeCommandService;

    private final long postId = 1L;
    private final long userId = 2L;
    private final long authorId = 3L;
    private final long columnId = 4L;

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
                Like postLike = Like.builder()
                        .id(1L)
                        .postId(postId)
                        .userId(userId)
                        .isDelete(false)
                        .build();

                when(postService.getReportCountByPostId(postId)).thenReturn(0); // 게시글 존재 확인
                when(postService.getWriterIdByPostId(postId)).thenReturn(authorId);
                when(likeRepository.findByPostIdAndUserIdAndIsDeleteFalse(postId, userId)).thenReturn(Optional.empty());
                when(likeRepository.save(any(Like.class))).thenReturn(postLike);
                when(likeRepository.countByPostIdAndIsDeleteFalse(postId)).thenReturn(11);

                // When
                PostLikeResponse response = likeCommandService.togglePostLike(postId, userId);

                // Then
                assertNotNull(response);
                assertTrue(response.isLiked());
                assertEquals(11, response.getTotalLikes());

                verify(postService).increaseLikeCount(postId);
                verify(pointRewardService).givePointIfFirstLike(postId, userId, authorId);
                // 11은 피보나치 수가 아니므로 알림이 발송되지 않아야 함
                verify(notificationCommandService, never()).sendNotification(any());
            }
            
            @Test
            @DisplayName("좋아요 수가 피보나치 수(5)일 때 알림이 발송되어야 한다")
            void shouldSendNotificationWhenLikeCountIsFibonacci() {
                // Given
                Like postLike = Like.builder()
                        .id(1L)
                        .postId(postId)
                        .userId(userId)
                        .isDelete(false)
                        .build();

                String postTitle = "테스트 게시글";
                
                when(postService.getReportCountByPostId(postId)).thenReturn(0);
                when(postService.getWriterIdByPostId(postId)).thenReturn(authorId);
                when(postService.getPostTitle(postId)).thenReturn(postTitle);
                when(likeRepository.findByPostIdAndUserIdAndIsDeleteFalse(postId, userId)).thenReturn(Optional.empty());
                when(likeRepository.save(any(Like.class))).thenReturn(postLike);
                when(likeRepository.countByPostIdAndIsDeleteFalse(postId)).thenReturn(5); // 피보나치 수

                // When
                PostLikeResponse response = likeCommandService.togglePostLike(postId, userId);

                // Then
                assertNotNull(response);
                assertTrue(response.isLiked());
                assertEquals(5, response.getTotalLikes());

                verify(postService).increaseLikeCount(postId);
                verify(pointRewardService).givePointIfFirstLike(postId, userId, authorId);
                // 5는 피보나치 수이므로 알림이 발송되어야 함
                verify(notificationCommandService).sendNotification(any());
            }
        }

        @Nested
        @DisplayName("좋아요 취소 시")
        class CancelLike {

            @Test
            @DisplayName("좋아요 수를 감소시키고 소프트 딜리트해야 한다")
            void shouldDecreaseLikeCountAndSoftDelete() {
                // Given
                Like postLike = Like.builder()
                        .id(1L)
                        .postId(postId)
                        .userId(userId)
                        .isDelete(false)
                        .build();

                when(postService.getReportCountByPostId(postId)).thenReturn(0); // 게시글 존재 확인
                when(likeRepository.findByPostIdAndUserIdAndIsDeleteFalse(postId, userId)).thenReturn(Optional.of(postLike));
                when(likeRepository.countByPostIdAndIsDeleteFalse(postId)).thenReturn(9);

                // When
                PostLikeResponse response = likeCommandService.togglePostLike(postId, userId);

                // Then
                assertNotNull(response);
                assertFalse(response.isLiked());
                assertEquals(9, response.getTotalLikes());

                verify(likeRepository).save(postLike);
                verify(postService).decreaseLikeCount(postId);
                verify(pointRewardService, never()).givePointIfFirstLike(anyLong(), anyLong(), anyLong());
            }
        }

        @Nested
        @DisplayName("예외 처리")
        class HandleExceptions {

            @Test
            @DisplayName("게시글이 없으면 POST_NOT_FOUND 예외를 발생시켜야 한다")
            void shouldThrowExceptionWhenPostNotFound() {
                // Given
                when(postService.getReportCountByPostId(postId)).thenThrow(new BusinessException(ErrorCode.POST_NOT_FOUND));

                // When & Then
                BusinessException exception = assertThrows(BusinessException.class, () -> 
                    likeCommandService.togglePostLike(postId, userId));
                
                assertEquals(ErrorCode.POST_NOT_FOUND, exception.getErrorCode());
                verify(likeRepository, never()).save(any(Like.class));
                verify(pointRewardService, never()).givePointIfFirstLike(anyLong(), anyLong(), anyLong());
            }
        }
    }
    
    @Nested
    @DisplayName("toggleColumnLike Method")
    class ToggleColumnLike {

        @Nested
        @DisplayName("좋아요 추가 시")
        class AddLike {

            @Test
            @DisplayName("좋아요를 추가하고 칼럼 좋아요 수를 증가시켜야 한다")
            void shouldAddLikeAndIncreaseColumnLikeCount() {
                // Given
                Column column = mock(Column.class);
                Like columnLike = Like.builder()
                        .id(2L)
                        .columnId(columnId)
                        .userId(userId)
                        .isDelete(false)
                        .build();

                when(columnService.getColumn(columnId)).thenReturn(column);
                when(columnService.isColumnAuthor(columnId, userId)).thenReturn(false);
                when(likeRepository.findByColumnIdAndUserIdAndIsDeleteFalse(columnId, userId)).thenReturn(Optional.empty());
                when(likeRepository.save(any(Like.class))).thenReturn(columnLike);
                when(likeRepository.countByColumnIdAndIsDeleteFalse(columnId)).thenReturn(11);

                // When
                ColumnLikeResponse response = likeCommandService.toggleColumnLike(columnId, userId);

                // Then
                assertNotNull(response);
                assertTrue(response.isLiked());
                
                verify(columnService).increaseLikeCount(columnId);
                // 11은 피보나치 수가 아니므로 알림이 발송되지 않아야 함
                verify(notificationCommandService, never()).sendNotification(any());
                // 포인트 지급이 없으므로 포인트 서비스는 호출되지 않아야 함
                verify(pointRewardService, never()).givePointIfFirstLike(anyLong(), anyLong(), anyLong());
            }
            
            @Test
            @DisplayName("좋아요 수가 피보나치 수(8)일 때 알림이 발송되어야 한다")
            void shouldSendNotificationWhenLikeCountIsFibonacci() {
                // Given
                Column column = mock(Column.class);
                Like columnLike = Like.builder()
                        .id(2L)
                        .columnId(columnId)
                        .userId(userId)
                        .isDelete(false)
                        .build();
                
                String columnTitle = "테스트 칼럼";
                
                when(columnService.getColumn(columnId)).thenReturn(column);
                when(column.getMentorId()).thenReturn(5L);
                when(columnService.isColumnAuthor(columnId, userId)).thenReturn(false);
                when(columnService.getColumnTitle(columnId)).thenReturn(columnTitle);
                when(columnService.getUserIdByMentorId(5L)).thenReturn(authorId);
                when(likeRepository.findByColumnIdAndUserIdAndIsDeleteFalse(columnId, userId)).thenReturn(Optional.empty());
                when(likeRepository.save(any(Like.class))).thenReturn(columnLike);
                when(likeRepository.countByColumnIdAndIsDeleteFalse(columnId)).thenReturn(8); // 피보나치 수

                // When
                ColumnLikeResponse response = likeCommandService.toggleColumnLike(columnId, userId);

                // Then
                assertNotNull(response);
                assertTrue(response.isLiked());
                
                verify(columnService).increaseLikeCount(columnId);
                // 8은 피보나치 수이므로 알림이 발송되어야 함
                verify(notificationCommandService).sendNotification(any());
            }
        }

        @Nested
        @DisplayName("좋아요 취소 시")
        class CancelLike {

            @Test
            @DisplayName("좋아요 수를 감소시키고 소프트 딜리트해야 한다")
            void shouldDecreaseLikeCountAndSoftDelete() {
                // Given
                Column column = mock(Column.class);
                Like columnLike = Like.builder()
                        .id(2L)
                        .columnId(columnId)
                        .userId(userId)
                        .isDelete(false)
                        .build();

                when(columnService.getColumn(columnId)).thenReturn(column);
                when(columnService.isColumnAuthor(columnId, userId)).thenReturn(false);
                when(likeRepository.findByColumnIdAndUserIdAndIsDeleteFalse(columnId, userId)).thenReturn(Optional.of(columnLike));
                when(likeRepository.countByColumnIdAndIsDeleteFalse(columnId)).thenReturn(8);

                // When
                ColumnLikeResponse response = likeCommandService.toggleColumnLike(columnId, userId);

                // Then
                assertNotNull(response);
                assertFalse(response.isLiked());
                
                verify(likeRepository).save(columnLike);
                verify(columnService).decreaseLikeCount(columnId);
            }
        }

        @Nested
        @DisplayName("예외 처리")
        class HandleExceptions {

            @Test
            @DisplayName("칼럼이 없으면 COLUMN_NOT_FOUND 예외를 발생시켜야 한다")
            void shouldThrowExceptionWhenColumnNotFound() {
                // Given
                when(columnService.getColumn(columnId)).thenThrow(new BusinessException(ErrorCode.COLUMN_NOT_FOUND));

                // When & Then
                BusinessException exception = assertThrows(BusinessException.class, () -> 
                    likeCommandService.toggleColumnLike(columnId, userId));
                
                assertEquals(ErrorCode.COLUMN_NOT_FOUND, exception.getErrorCode());
                verify(likeRepository, never()).save(any(Like.class));
            }
        }
    }
} 