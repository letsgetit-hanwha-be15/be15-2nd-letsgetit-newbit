package com.newbit.newbitfeatureservice.coffeechat.command.application.service;

import com.newbit.newbitfeatureservice.coffeechat.command.application.dto.request.ReviewCreateRequest;
import com.newbit.newbitfeatureservice.coffeechat.command.domain.aggregate.Review;
import com.newbit.newbitfeatureservice.coffeechat.command.domain.repository.ReviewRepository;
import com.newbit.newbitfeatureservice.coffeechat.query.dto.response.CoffeechatDetailResponse;
import com.newbit.newbitfeatureservice.coffeechat.query.dto.response.CoffeechatDto;
import com.newbit.newbitfeatureservice.coffeechat.query.dto.response.ProgressStatus;
import com.newbit.newbitfeatureservice.coffeechat.query.service.CoffeechatQueryService;
import com.newbit.newbitfeatureservice.common.exception.BusinessException;
import com.newbit.newbitfeatureservice.common.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewInternalServiceTest {

    @InjectMocks
    private ReviewInternalService reviewInternalService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private CoffeechatQueryService coffeechatQueryService;

    @DisplayName("정상적으로 리뷰가 저장되는 경우")
    @Test
    void saveReview_success() {
        Long userId = 1L;
        Long coffeechatId = 10L;
        ReviewCreateRequest request = new ReviewCreateRequest(4.5, "좋았어요", 20, coffeechatId);

        CoffeechatDto coffeechatDto = CoffeechatDto.builder()
                .coffeechatId(coffeechatId)
                .menteeId(userId)
                .mentorId(2L)
                .progressStatus(ProgressStatus.COMPLETE)
                .build();

        when(coffeechatQueryService.getCoffeechat(coffeechatId)).thenReturn(
                CoffeechatDetailResponse.builder().coffeechat(coffeechatDto).build());
        when(reviewRepository.findByCoffeechatId(coffeechatId)).thenReturn(Optional.empty());

        Review review = Review.of(4.5, "좋았어요", 20, coffeechatId, userId);
        ReflectionTestUtils.setField(review, "reviewId", 100L);
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        Review savedReview = reviewInternalService.saveReview(userId, request);

        assertThat(savedReview.getReviewId()).isEqualTo(100L);
        assertThat(savedReview.getComment()).isEqualTo("좋았어요");
        verify(reviewRepository).save(any(Review.class));
    }

    @DisplayName("진행 중인 커피챗일 경우 예외 발생")
    @Test
    void saveReview_invalidStatus() {
        Long userId = 1L;
        Long coffeechatId = 10L;
        ReviewCreateRequest request = new ReviewCreateRequest(4.5, "좋았어요", 20, coffeechatId);

        CoffeechatDto coffeechatDto = CoffeechatDto.builder()
                .coffeechatId(coffeechatId)
                .menteeId(userId)
                .mentorId(2L)
                .progressStatus(ProgressStatus.IN_PROGRESS)
                .build();

        when(coffeechatQueryService.getCoffeechat(coffeechatId))
                .thenReturn(CoffeechatDetailResponse.builder().coffeechat(coffeechatDto).build());

        BusinessException exception = assertThrows(BusinessException.class,
                () -> reviewInternalService.saveReview(userId, request));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_COFFEECHAT_STATUS_COMPLETE);
    }

    @DisplayName("다른 사용자가 리뷰 작성 시 예외 발생")
    @Test
    void saveReview_invalidMentee() {
        Long userId = 1L;
        Long coffeechatId = 10L;
        ReviewCreateRequest request = new ReviewCreateRequest(4.5, "좋았어요", 20, coffeechatId);

        CoffeechatDto coffeechatDto = CoffeechatDto.builder()
                .coffeechatId(coffeechatId)
                .menteeId(999L)
                .mentorId(2L)
                .progressStatus(ProgressStatus.COMPLETE)
                .build();

        when(coffeechatQueryService.getCoffeechat(coffeechatId))
                .thenReturn(CoffeechatDetailResponse.builder().coffeechat(coffeechatDto).build());

        BusinessException exception = assertThrows(BusinessException.class,
                () -> reviewInternalService.saveReview(userId, request));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.REVIEW_CREATE_NOT_ALLOWED);
    }

    @DisplayName("이미 리뷰가 존재하는 경우 예외 발생")
    @Test
    void saveReview_alreadyExists() {
        Long userId = 1L;
        Long coffeechatId = 10L;
        ReviewCreateRequest request = new ReviewCreateRequest(4.5, "좋았어요", 20, coffeechatId);

        CoffeechatDto coffeechatDto = CoffeechatDto.builder()
                .coffeechatId(coffeechatId)
                .menteeId(userId)
                .mentorId(2L)
                .progressStatus(ProgressStatus.COMPLETE)
                .build();

        when(coffeechatQueryService.getCoffeechat(coffeechatId))
                .thenReturn(CoffeechatDetailResponse.builder().coffeechat(coffeechatDto).build());
        when(reviewRepository.findByCoffeechatId(coffeechatId)).thenReturn(Optional.of(mock(Review.class)));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> reviewInternalService.saveReview(userId, request));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.REVIEW_ALREADY_EXIST);
    }
}