package com.newbit.newbitfeatureservice.coffeechat.command.application.service;

import com.newbit.newbitfeatureservice.client.user.MentorFeignClient;
import com.newbit.newbitfeatureservice.coffeechat.command.application.dto.request.ReviewCreateRequest;
import com.newbit.newbitfeatureservice.coffeechat.command.domain.aggregate.Review;
import com.newbit.newbitfeatureservice.coffeechat.command.domain.repository.ReviewRepository;
import com.newbit.newbitfeatureservice.coffeechat.query.dto.response.CoffeechatDto;
import com.newbit.newbitfeatureservice.coffeechat.query.dto.response.ProgressStatus;
import com.newbit.newbitfeatureservice.coffeechat.query.service.CoffeechatQueryService;
import com.newbit.newbitfeatureservice.common.exception.BusinessException;
import com.newbit.newbitfeatureservice.common.exception.ErrorCode;
import com.newbit.newbitfeatureservice.purchase.command.application.service.PointTransactionCommandService;
import com.newbit.newbitfeatureservice.purchase.command.domain.PointTypeConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewCommandService {

    private final ReviewRepository reviewRepository;
    private final CoffeechatQueryService coffeechatQueryService;
    private final PointTransactionCommandService pointTransactionCommandService;
    private final MentorFeignClient mentorClient;
    private final ReviewInternalService reviewInternalService;

    public Long createReview(Long userId, ReviewCreateRequest request) {
        // 트랜잭션 처리: 리뷰 저장 및 검증 로직
        Review review = reviewInternalService.saveReview(userId, request);

        // 트랜잭션 이후 외부 호출 처리
        CoffeechatDto coffeechat = coffeechatQueryService.getCoffeechat(request.getCoffeechatId()).getCoffeechat();

        if (request.getTip() != null) {
            Long mentorUserId = mentorClient.getUserIdByMentorId(coffeechat.getMentorId()).getData();
            pointTransactionCommandService.giveTipPoint(
                    request.getCoffeechatId(), userId, mentorUserId, request.getTip()
            );
        }

        if (request.getComment() != null) {
            pointTransactionCommandService.givePointByType(
                    userId, PointTypeConstants.REVIEW, review.getReviewId()
            );
        }

        return review.getReviewId();
    }

    @Transactional
    public void deleteReview(Long userId, Long reviewId) {
        // 1. 리뷰가 존재하는지 확인
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND));

        // 2. 본인이 작성한 리뷰인지 확인
        if(!review.getUserId().equals(userId)){
            throw new BusinessException(ErrorCode.REVIEW_CANCEL_NOT_ALLOWED);
        }

        // 3. 리뷰 삭제
        review.delete();
    }
}
