package com.newbit.newbitfeatureservice.coffeechat.command.application.service;

import com.newbit.newbitfeatureservice.client.user.MentorFeignClient;
import com.newbit.newbitfeatureservice.coffeechat.command.application.dto.request.ReviewCreateRequest;
import com.newbit.newbitfeatureservice.coffeechat.command.domain.aggregate.Review;
import com.newbit.newbitfeatureservice.coffeechat.command.domain.repository.ReviewRepository;
import com.newbit.newbitfeatureservice.coffeechat.query.dto.response.CoffeechatDetailResponse;
import com.newbit.newbitfeatureservice.coffeechat.query.dto.response.CoffeechatDto;
import com.newbit.newbitfeatureservice.coffeechat.query.dto.response.ProgressStatus;
import com.newbit.newbitfeatureservice.coffeechat.query.service.CoffeechatQueryService;
import com.newbit.newbitfeatureservice.common.exception.BusinessException;
import com.newbit.newbitfeatureservice.common.exception.ErrorCode;
import com.newbit.newbitfeatureservice.purchase.command.application.service.PointTransactionCommandService;
import com.newbit.newbitfeatureservice.purchase.command.domain.PointTypeConstants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Optional;

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


    @DisplayName("내용o, 팁o 리뷰 등록 성공")
    @Test
    void createReview_성공() {
        // given
        Long userId = 8L;
        Long mentorId = 2L;
        double rating = 5.0;
        String comment = "진짜 좋았어요 짱짱,진짜 좋았어요 짱짱,진짜 좋았어요 짱짱,진짜 좋았어요 짱짱,진짜 좋았어요 짱짱";
        Integer tip = 30;
        Long coffeechatId = 2L;
        Long reviewId = 1L;

        ReviewCreateRequest request = new ReviewCreateRequest(
                rating,
                comment,
                tip,
                coffeechatId
        );

        CoffeechatDto coffeechatDto = CoffeechatDto.builder()
                .coffeechatId(coffeechatId)
                .mentorId(mentorId)
                .menteeId(userId)
                .progressStatus(ProgressStatus.COMPLETE)
                .build();

        Review review = Review.of(
                rating, comment, tip, coffeechatId,
                userId);
        ReflectionTestUtils.setField(review, "reviewId", reviewId);

        when(coffeechatQueryService.getCoffeechat(coffeechatId))
                .thenReturn(CoffeechatDetailResponse
                        .builder()
                        .coffeechat(coffeechatDto)
                        .build());
        when(reviewRepository.findByCoffeechatId(coffeechatId)).thenReturn(Optional.empty());
        when(reviewRepository.save(any(Review.class))).thenReturn(review);
        when(mentorClient.getUserIdByMentorId(mentorId).getData()).thenReturn(5L);
        doNothing().when(pointTransactionCommandService).giveTipPoint(coffeechatId, userId, 5L, tip);
        doNothing().when(pointTransactionCommandService).givePointByType(userId, PointTypeConstants.REVIEW, review.getReviewId());

        // when & then
        assertDoesNotThrow(() -> reviewCommandService.createReview(userId, request));
        // 검증
        verify(coffeechatQueryService).getCoffeechat(coffeechatId);
        verify(reviewRepository).findByCoffeechatId(coffeechatId);
        verify(reviewRepository).save(any(Review.class));
        verify(pointTransactionCommandService).giveTipPoint(coffeechatId, userId, 5L, tip);
        verify(pointTransactionCommandService).givePointByType(userId, PointTypeConstants.REVIEW, reviewId);

        ArgumentCaptor<Review> reviewCaptor = ArgumentCaptor.forClass(Review.class);
        verify(reviewRepository).save(reviewCaptor.capture());
        Review savedReview = reviewCaptor.getValue();
        assertEquals(BigDecimal.valueOf(rating), savedReview.getRating());
        assertEquals(comment, savedReview.getComment());
        assertEquals(tip, savedReview.getTip());
    }

    @DisplayName("내용이 없을 때 리뷰 등록 성공")
    @Test
    void createReview_withoutComment_success() {
        // given
        Long userId = 8L;
        Long mentorId = 2L;
        double rating = 4.5;
        String comment = null; // 내용 없음
        Integer tip = 30;
        Long coffeechatId = 3L;
        Long reviewId = 10L;

        ReviewCreateRequest request = new ReviewCreateRequest(rating, comment, tip, coffeechatId);

        CoffeechatDto coffeechatDto = CoffeechatDto.builder()
                .coffeechatId(coffeechatId)
                .mentorId(mentorId)
                .menteeId(userId)
                .progressStatus(ProgressStatus.COMPLETE)
                .build();

        Review review = Review.of(rating, comment, tip, coffeechatId, userId);
        ReflectionTestUtils.setField(review, "reviewId", reviewId);

        when(coffeechatQueryService.getCoffeechat(coffeechatId))
                .thenReturn(CoffeechatDetailResponse.builder()
                        .coffeechat(coffeechatDto)
                        .build());
        when(reviewRepository.findByCoffeechatId(coffeechatId)).thenReturn(Optional.empty());
        when(reviewRepository.save(any(Review.class))).thenReturn(review);
        // tip이 존재하므로 멘토의 유저 아이디 조회 및 팁 지급이 발생
        when(mentorClient.getUserIdByMentorId(mentorId).getData()).thenReturn(5L);
        doNothing().when(pointTransactionCommandService).giveTipPoint(coffeechatId, userId, 5L, tip);

        // 내용이 없으므로 리뷰 작성 포인트 지급은 발생하지 않아야 함
        // when & then
        assertDoesNotThrow(() -> reviewCommandService.createReview(userId, request));

        verify(coffeechatQueryService).getCoffeechat(coffeechatId);
        verify(reviewRepository).findByCoffeechatId(coffeechatId);
        verify(reviewRepository).save(any(Review.class));

        // 리뷰 코멘트가 없으므로 리뷰 적립 포인트는 호출되지 않아야 함
        verify(pointTransactionCommandService, never()).givePointByType(anyLong(), anyString(), anyLong());
        // 팁은 존재하므로 팁 지급 호출됨
        verify(pointTransactionCommandService).giveTipPoint(coffeechatId, userId, 5L, tip);

        ArgumentCaptor<Review> reviewCaptor = ArgumentCaptor.forClass(Review.class);
        verify(reviewRepository).save(reviewCaptor.capture());
        Review savedReview = reviewCaptor.getValue();
        assertEquals(BigDecimal.valueOf(rating), savedReview.getRating());
        assertNull(savedReview.getComment());
        assertEquals(tip, savedReview.getTip());
    }

    @DisplayName("팁이 없을 때 리뷰 등록 성공")
    @Test
    void createReview_withoutTip_success() {
        // given
        Long userId = 8L;
        Long mentorId = 2L;
        double rating = 4.7;
        String comment = "유익한 리뷰";
        Integer tip = null; // 팁 없음
        Long coffeechatId = 4L;
        Long reviewId = 11L;

        ReviewCreateRequest request = new ReviewCreateRequest(rating, comment, tip, coffeechatId);

        CoffeechatDto coffeechatDto = CoffeechatDto.builder()
                .coffeechatId(coffeechatId)
                .mentorId(mentorId)
                .menteeId(userId)
                .progressStatus(ProgressStatus.COMPLETE)
                .build();

        Review review = Review.of(rating, comment, tip, coffeechatId, userId);
        ReflectionTestUtils.setField(review, "reviewId", reviewId);

        when(coffeechatQueryService.getCoffeechat(coffeechatId))
                .thenReturn(CoffeechatDetailResponse.builder()
                        .coffeechat(coffeechatDto)
                        .build());
        when(reviewRepository.findByCoffeechatId(coffeechatId)).thenReturn(Optional.empty());
        when(reviewRepository.save(any(Review.class))).thenReturn(review);
        // tip이 없으므로 giveTipPoint 호출은 발생하지 않아야 함.
        doNothing().when(pointTransactionCommandService).givePointByType(userId, PointTypeConstants.REVIEW, reviewId);

        // when & then
        assertDoesNotThrow(() -> reviewCommandService.createReview(userId, request));

        verify(coffeechatQueryService).getCoffeechat(coffeechatId);
        verify(reviewRepository).findByCoffeechatId(coffeechatId);
        verify(reviewRepository).save(any(Review.class));

        // 팁이 없으므로 팁 지급은 호출되지 않아야 함
        verify(pointTransactionCommandService, never()).giveTipPoint(anyLong(), anyLong(), anyLong(), any());
        // 내용이 존재하므로 리뷰 적립 포인트 지급은 호출됨
        verify(pointTransactionCommandService).givePointByType(userId, PointTypeConstants.REVIEW, reviewId);

        ArgumentCaptor<Review> reviewCaptor = ArgumentCaptor.forClass(Review.class);
        verify(reviewRepository).save(reviewCaptor.capture());
        Review savedReview = reviewCaptor.getValue();
        assertEquals(BigDecimal.valueOf(rating), savedReview.getRating());
        assertEquals(comment, savedReview.getComment());
        assertNull(savedReview.getTip());
    }

    @DisplayName("내용과 팁 둘 다 없을 때 리뷰 등록 성공")
    @Test
    void createReview_withoutCommentAndTip_success() {
        // given
        Long userId = 8L;
        Long mentorId = 2L;
        double rating = 4.0;
        String comment = null; // 내용 없음
        Integer tip = null;    // 팁 없음
        Long coffeechatId = 5L;
        Long reviewId = 12L;

        ReviewCreateRequest request = new ReviewCreateRequest(rating, comment, tip, coffeechatId);

        CoffeechatDto coffeechatDto = CoffeechatDto.builder()
                .coffeechatId(coffeechatId)
                .mentorId(mentorId)
                .menteeId(userId)
                .progressStatus(ProgressStatus.COMPLETE)
                .build();

        Review review = Review.of(rating, comment, tip, coffeechatId, userId);
        ReflectionTestUtils.setField(review, "reviewId", reviewId);

        when(coffeechatQueryService.getCoffeechat(coffeechatId))
                .thenReturn(CoffeechatDetailResponse.builder()
                        .coffeechat(coffeechatDto)
                        .build());
        when(reviewRepository.findByCoffeechatId(coffeechatId)).thenReturn(Optional.empty());
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        // when & then
        assertDoesNotThrow(() -> reviewCommandService.createReview(userId, request));

        verify(coffeechatQueryService).getCoffeechat(coffeechatId);
        verify(reviewRepository).findByCoffeechatId(coffeechatId);
        verify(reviewRepository).save(any(Review.class));

        // 내용과 팁 둘 다 없으므로 두 포인트 트랜잭션 메서드 모두 호출되지 않아야 함
        verify(pointTransactionCommandService, never()).giveTipPoint(anyLong(), anyLong(), anyLong(), any());
        verify(pointTransactionCommandService, never()).givePointByType(anyLong(), anyString(), anyLong());

        ArgumentCaptor<Review> reviewCaptor = ArgumentCaptor.forClass(Review.class);
        verify(reviewRepository).save(reviewCaptor.capture());
        Review savedReview = reviewCaptor.getValue();
        assertEquals(BigDecimal.valueOf(rating), savedReview.getRating());
        assertNull(savedReview.getComment());
        assertNull(savedReview.getTip());
    }

    @DisplayName("완료되지 않은 커피챗일 때 에러 발생 및 에러 코드 검증")
    @Test
    void createReview_incompleteCoffeechat_throwsBusinessException() {
        // given
        Long userId = 8L;
        double rating = 5.0;
        String comment = "좋았어요";
        Integer tip = 20;
        Long coffeechatId = 6L;

        // 진행 상태가 COMPLETE가 아닌 경우 (예: IN_PROGRESS)
        CoffeechatDto coffeechatDto = CoffeechatDto.builder()
                .coffeechatId(coffeechatId)
                .mentorId(3L)
                .menteeId(userId)
                .progressStatus(ProgressStatus.IN_PROGRESS)  // COMPLETE가 아님
                .build();

        ReviewCreateRequest request = new ReviewCreateRequest(rating, comment, tip, coffeechatId);

        when(coffeechatQueryService.getCoffeechat(coffeechatId))
                .thenReturn(CoffeechatDetailResponse.builder()
                        .coffeechat(coffeechatDto)
                        .build());

        // when & then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> reviewCommandService.createReview(userId, request));
        // 에러 코드 검증
        assertEquals(ErrorCode.INVALID_COFFEECHAT_STATUS_COMPLETE, exception.getErrorCode());
    }

    @DisplayName("커피챗의 멘티가 아닐 때 에러 발생 및 에러 코드 검증")
    @Test
    void createReview_notMentee_throwsBusinessException() {
        // given
        Long userId = 8L;
        double rating = 5.0;
        String comment = "괜찮았어요";
        Integer tip = 20;
        Long coffeechatId = 7L;

        // 커피챗의 멘티가 요청한 userId와 다른 경우
        CoffeechatDto coffeechatDto = CoffeechatDto.builder()
                .coffeechatId(coffeechatId)
                .mentorId(3L)
                .menteeId(9L) // userId(8L)와 다름
                .progressStatus(ProgressStatus.COMPLETE)
                .build();

        ReviewCreateRequest request = new ReviewCreateRequest(rating, comment, tip, coffeechatId);

        when(coffeechatQueryService.getCoffeechat(coffeechatId))
                .thenReturn(CoffeechatDetailResponse.builder()
                        .coffeechat(coffeechatDto)
                        .build());

        // when & then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> reviewCommandService.createReview(userId, request));
        // 에러 코드 검증
        assertEquals(ErrorCode.REVIEW_CREATE_NOT_ALLOWED, exception.getErrorCode());
    }

    @DisplayName("해당 커피챗에 대한 리뷰가 이미 존재할 때 에러 발생 및 에러 코드 검증")
    @Test
    void createReview_reviewAlreadyExists_throwsBusinessException() {
        // given
        Long userId = 8L;
        double rating = 5.0;
        String comment = "정말 좋았어요";
        Integer tip = 20;
        Long coffeechatId = 8L;

        // 정상적인 커피챗 데이터 생성
        CoffeechatDto coffeechatDto = CoffeechatDto.builder()
                .coffeechatId(coffeechatId)
                .mentorId(3L)
                .menteeId(userId)
                .progressStatus(ProgressStatus.COMPLETE)
                .build();

        ReviewCreateRequest request = new ReviewCreateRequest(rating, comment, tip, coffeechatId);

        when(coffeechatQueryService.getCoffeechat(coffeechatId))
                .thenReturn(CoffeechatDetailResponse.builder()
                        .coffeechat(coffeechatDto)
                        .build());

        // 해당 커피챗에 이미 리뷰가 존재하는 경우
        Review existingReview = Review.of(rating, comment, tip, coffeechatId, userId);
        ReflectionTestUtils.setField(existingReview, "reviewId", 99L);
        when(reviewRepository.findByCoffeechatId(coffeechatId))
                .thenReturn(Optional.of(existingReview));

        // when & then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> reviewCommandService.createReview(userId, request));
        assertEquals(ErrorCode.REVIEW_ALREADY_EXIST, exception.getErrorCode());
    }


    @DisplayName("정상적으로 리뷰 삭제 성공")
    @Test
    void deleteReview_success() {
        // given
        Long userId = 8L;
        Long reviewId = 10L;

        // 리뷰 객체 생성 시, 리뷰의 작성자ID userId와 일치해야 정상 삭제가 이루어짐
        Review review = mock(Review.class);
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(review.getUserId()).thenReturn(userId);

        // when & then
        assertDoesNotThrow(() -> reviewCommandService.deleteReview(userId, reviewId));

        // 삭제 로직 검증 (리뷰의 delete() 메서드가 호출되었는지 확인)
        verify(review, times(1)).delete();
    }

    @DisplayName("리뷰가 존재하지 않을 때 에러 발생 및 에러 코드 검증")
    @Test
    void deleteReview_reviewNotFound_throwsBusinessException() {
        // given
        Long userId = 8L;
        Long reviewId = 10L;

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () ->
                reviewCommandService.deleteReview(userId, reviewId)
        );
        // 에러 코드 검증 : 리뷰가 존재하지 않음
        assertEquals(ErrorCode.REVIEW_NOT_FOUND, exception.getErrorCode());
    }

    @DisplayName("본인이 작성한 리뷰가 아닐 때 에러 발생 및 에러 코드 검증")
    @Test
    void deleteReview_notOwner_throwsBusinessException() {
        // given
        Long userId = 8L;
        Long reviewId = 10L;

        // 리뷰 객체 생성 시, userId와 요청한 userId와 다르게 설정하여 본인이 작성한 리뷰가 아님을 표현
        Review review = mock(Review.class);
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(review.getUserId()).thenReturn(9L); // userId 8L와 다름

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () ->
                reviewCommandService.deleteReview(userId, reviewId)
        );
        // 에러 코드 검증 : 본인이 작성한 리뷰가 아님
        assertEquals(ErrorCode.REVIEW_CANCEL_NOT_ALLOWED, exception.getErrorCode());
    }
}