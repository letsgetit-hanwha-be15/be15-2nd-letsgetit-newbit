package com.newbit.newbitfeatureservice.coffeechat.command.application.service;

import com.newbit.newbitfeatureservice.client.user.MentorFeignClient;
import com.newbit.newbitfeatureservice.coffeechat.command.application.dto.request.ReviewCreateRequest;
import com.newbit.newbitfeatureservice.coffeechat.command.domain.aggregate.Review;
import com.newbit.newbitfeatureservice.coffeechat.command.domain.repository.ReviewRepository;
import com.newbit.newbitfeatureservice.coffeechat.query.dto.response.CoffeechatDetailResponse;
import com.newbit.newbitfeatureservice.coffeechat.query.dto.response.CoffeechatDto;
import com.newbit.newbitfeatureservice.coffeechat.query.dto.response.ProgressStatus;
import com.newbit.newbitfeatureservice.coffeechat.query.service.CoffeechatQueryService;
import com.newbit.newbitfeatureservice.common.dto.ApiResponse;
import com.newbit.newbitfeatureservice.common.exception.BusinessException;
import com.newbit.newbitfeatureservice.common.exception.ErrorCode;
import com.newbit.newbitfeatureservice.purchase.command.application.service.PointTransactionCommandService;
import com.newbit.newbitfeatureservice.purchase.command.domain.PointTypeConstants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewCommandServiceTest {

    @InjectMocks
    private ReviewCommandService reviewCommandService;

    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private CoffeechatQueryService coffeechatQueryService;
    @Mock
    private MentorFeignClient mentorClient;
    @Mock
    private PointTransactionCommandService pointTransactionCommandService;
    @Mock
    private ReviewInternalService reviewInternalService;

    @DisplayName("내용o, 팁o 리뷰 등록 성공")
    @Test
    void createReview_success() {
        Long userId = 8L;
        Long mentorId = 2L;
        double rating = 5.0;
        String comment = "진짜 좋았어요 짱짱,진짜 좋았어요 짱짱,진짜 좋았어요 짱짱,진짜 좋았어요 짱짱,진짜 좋았어요 짱짱";
        Integer tip = 30;
        Long coffeechatId = 2L;
        Long reviewId = 1L;

        ReviewCreateRequest request = new ReviewCreateRequest(rating, comment, tip, coffeechatId);
        CoffeechatDto coffeechatDto = CoffeechatDto.builder()
                .coffeechatId(coffeechatId)
                .mentorId(mentorId)
                .menteeId(userId)
                .progressStatus(ProgressStatus.COMPLETE)
                .build();
        Review review = Review.of(rating, comment, tip, coffeechatId, userId);
        ReflectionTestUtils.setField(review, "reviewId", reviewId);

        when(reviewInternalService.saveReview(userId, request)).thenReturn(review);
        when(coffeechatQueryService.getCoffeechat(coffeechatId)).thenReturn(CoffeechatDetailResponse.builder().coffeechat(coffeechatDto).build());
        when(mentorClient.getUserIdByMentorId(mentorId)).thenReturn(ApiResponse.success(5L));
        doNothing().when(pointTransactionCommandService).giveTipPoint(coffeechatId, userId, 5L, tip);
        doNothing().when(pointTransactionCommandService).givePointByType(userId, PointTypeConstants.REVIEW, reviewId);

        assertDoesNotThrow(() -> reviewCommandService.createReview(userId, request));
        verify(reviewInternalService).saveReview(userId, request);
        verify(coffeechatQueryService).getCoffeechat(coffeechatId);
        verify(pointTransactionCommandService).giveTipPoint(coffeechatId, userId, 5L, tip);
        verify(pointTransactionCommandService).givePointByType(userId, PointTypeConstants.REVIEW, reviewId);
    }

    @DisplayName("내용이 없을 때 리뷰 등록 성공")
    @Test
    void createReview_withoutComment_success() {
        Long userId = 8L;
        Long mentorId = 2L;
        double rating = 4.5;
        Integer tip = 30;
        Long coffeechatId = 3L;
        Long reviewId = 10L;

        ReviewCreateRequest request = new ReviewCreateRequest(rating, null, tip, coffeechatId);
        CoffeechatDto coffeechatDto = CoffeechatDto.builder()
                .coffeechatId(coffeechatId)
                .mentorId(mentorId)
                .menteeId(userId)
                .progressStatus(ProgressStatus.COMPLETE)
                .build();
        Review review = Review.of(rating, null, tip, coffeechatId, userId);
        ReflectionTestUtils.setField(review, "reviewId", reviewId);

        when(reviewInternalService.saveReview(userId, request)).thenReturn(review);
        when(coffeechatQueryService.getCoffeechat(coffeechatId)).thenReturn(CoffeechatDetailResponse.builder().coffeechat(coffeechatDto).build());
        when(mentorClient.getUserIdByMentorId(mentorId)).thenReturn(ApiResponse.success(5L));
        doNothing().when(pointTransactionCommandService).giveTipPoint(coffeechatId, userId, 5L, tip);

        assertDoesNotThrow(() -> reviewCommandService.createReview(userId, request));
        verify(reviewInternalService).saveReview(userId, request);
        verify(coffeechatQueryService).getCoffeechat(coffeechatId);
        verify(pointTransactionCommandService).giveTipPoint(coffeechatId, userId, 5L, tip);
        verify(pointTransactionCommandService, never()).givePointByType(anyLong(), anyString(), anyLong());
    }

    @DisplayName("팁이 없을 때 리뷰 등록 성공")
    @Test
    void createReview_withoutTip_success() {
        Long userId = 8L;
        Long mentorId = 2L;
        double rating = 4.7;
        String comment = "유익한 리뷰";
        Long coffeechatId = 4L;
        Long reviewId = 11L;

        ReviewCreateRequest request = new ReviewCreateRequest(rating, comment, null, coffeechatId);
        CoffeechatDto coffeechatDto = CoffeechatDto.builder()
                .coffeechatId(coffeechatId)
                .mentorId(mentorId)
                .menteeId(userId)
                .progressStatus(ProgressStatus.COMPLETE)
                .build();
        Review review = Review.of(rating, comment, null, coffeechatId, userId);
        ReflectionTestUtils.setField(review, "reviewId", reviewId);

        when(reviewInternalService.saveReview(userId, request)).thenReturn(review);
        when(coffeechatQueryService.getCoffeechat(coffeechatId)).thenReturn(CoffeechatDetailResponse.builder().coffeechat(coffeechatDto).build());
        doNothing().when(pointTransactionCommandService).givePointByType(userId, PointTypeConstants.REVIEW, reviewId);

        assertDoesNotThrow(() -> reviewCommandService.createReview(userId, request));
        verify(reviewInternalService).saveReview(userId, request);
        verify(coffeechatQueryService).getCoffeechat(coffeechatId);
        verify(pointTransactionCommandService, never()).giveTipPoint(anyLong(), anyLong(), anyLong(), any());
        verify(pointTransactionCommandService).givePointByType(userId, PointTypeConstants.REVIEW, reviewId);
    }

    @DisplayName("내용과 팁 둘 다 없을 때 리뷰 등록 성공")
    @Test
    void createReview_withoutCommentAndTip_success() {
        Long userId = 8L;
        Long mentorId = 2L;
        double rating = 4.0;
        Long coffeechatId = 5L;
        Long reviewId = 12L;

        ReviewCreateRequest request = new ReviewCreateRequest(rating, null, null, coffeechatId);
        CoffeechatDto coffeechatDto = CoffeechatDto.builder()
                .coffeechatId(coffeechatId)
                .mentorId(mentorId)
                .menteeId(userId)
                .progressStatus(ProgressStatus.COMPLETE)
                .build();
        Review review = Review.of(rating, null, null, coffeechatId, userId);
        ReflectionTestUtils.setField(review, "reviewId", reviewId);

        when(reviewInternalService.saveReview(userId, request)).thenReturn(review);
        when(coffeechatQueryService.getCoffeechat(coffeechatId)).thenReturn(CoffeechatDetailResponse.builder().coffeechat(coffeechatDto).build());

        assertDoesNotThrow(() -> reviewCommandService.createReview(userId, request));
        verify(reviewInternalService).saveReview(userId, request);
        verify(coffeechatQueryService).getCoffeechat(coffeechatId);
        verify(pointTransactionCommandService, never()).giveTipPoint(anyLong(), anyLong(), anyLong(), any());
        verify(pointTransactionCommandService, never()).givePointByType(anyLong(), anyString(), anyLong());
    }

    @DisplayName("정상적으로 리뷰 삭제 성공")
    @Test
    void deleteReview_success() {
        Long userId = 8L;
        Long reviewId = 10L;

        Review review = mock(Review.class);
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(review.getUserId()).thenReturn(userId);

        assertDoesNotThrow(() -> reviewCommandService.deleteReview(userId, reviewId));
        verify(review, times(1)).delete();
    }

    @DisplayName("리뷰가 존재하지 않을 때 에러 발생")
    @Test
    void deleteReview_reviewNotFound_throwsException() {
        Long userId = 8L;
        Long reviewId = 10L;

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                reviewCommandService.deleteReview(userId, reviewId));
        assertEquals(ErrorCode.REVIEW_NOT_FOUND, exception.getErrorCode());
    }

    @DisplayName("본인이 작성한 리뷰가 아닐 때 에러 발생")
    @Test
    void deleteReview_notOwner_throwsException() {
        Long userId = 8L;
        Long reviewId = 10L;

        Review review = mock(Review.class);
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(review.getUserId()).thenReturn(999L); // 다른 사용자

        BusinessException exception = assertThrows(BusinessException.class, () ->
                reviewCommandService.deleteReview(userId, reviewId));
        assertEquals(ErrorCode.REVIEW_CANCEL_NOT_ALLOWED, exception.getErrorCode());
    }
}