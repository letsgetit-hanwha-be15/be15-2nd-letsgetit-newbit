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

    @Transactional
    public Long createReview(Long userId, ReviewCreateRequest request) {
        // 1. 완료된 커피챗인지 확인
        CoffeechatDto coffeechatDto = coffeechatQueryService.getCoffeechat(request.getCoffeechatId())
                .getCoffeechat();
        if (!coffeechatDto.getProgressStatus().equals(ProgressStatus.COMPLETE)){
            throw new BusinessException(ErrorCode.INVALID_COFFEECHAT_STATUS_COMPLETE);
        }

        // 커피챗의 멘티가 맞는지 확인
        if (!coffeechatDto.getMenteeId().equals(userId)){
            throw new BusinessException(ErrorCode.REVIEW_CREATE_NOT_ALLOWED);
        }

        // 2. 해당 커피챗에 대한 리뷰가 이미 존재
        Optional<Review> existingReview = reviewRepository.findByCoffeechatId(request.getCoffeechatId());
        if (existingReview.isPresent()) {
            throw new BusinessException(ErrorCode.REVIEW_ALREADY_EXIST);
        }

        // 3. 리뷰 등록
        Review newReview = Review.of(
                request.getRating(),
                request.getComment(),
                request.getTip(),
                request.getCoffeechatId(),
                userId);
        Review review = reviewRepository.save(newReview);

        // 4. 팁이 존재하면 팁 등록
        if(request.getTip() != null) {
            // 멘토 아이디로 멘토의 유저 아이디를 찾아오기
            Long mentorId = mentorClient.getUserIdByMentorId(coffeechatDto.getMentorId()).getData();
            pointTransactionCommandService.giveTipPoint(
                    request.getCoffeechatId(), coffeechatDto.getMenteeId(), mentorId, request.getTip()
            );
        }

        // 5. 코멘트까지 등록 시 멘티에게 50포인트 지급
        if(request.getComment() != null) {
            pointTransactionCommandService.givePointByType(
                    coffeechatDto.getMenteeId(), PointTypeConstants.REVIEW, review.getReviewId()
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
