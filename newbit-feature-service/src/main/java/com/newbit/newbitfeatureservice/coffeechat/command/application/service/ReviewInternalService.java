package com.newbit.newbitfeatureservice.coffeechat.command.application.service;

import com.newbit.newbitfeatureservice.coffeechat.command.application.dto.request.ReviewCreateRequest;
import com.newbit.newbitfeatureservice.coffeechat.command.domain.aggregate.Review;
import com.newbit.newbitfeatureservice.coffeechat.command.domain.repository.ReviewRepository;
import com.newbit.newbitfeatureservice.coffeechat.query.dto.response.CoffeechatDto;
import com.newbit.newbitfeatureservice.coffeechat.query.dto.response.ProgressStatus;
import com.newbit.newbitfeatureservice.coffeechat.query.service.CoffeechatQueryService;
import com.newbit.newbitfeatureservice.common.exception.BusinessException;
import com.newbit.newbitfeatureservice.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewInternalService {

    private final ReviewRepository reviewRepository;
    private final CoffeechatQueryService coffeechatQueryService;

    @Transactional
    public Review saveReview(Long userId, ReviewCreateRequest request) {
        CoffeechatDto coffeechatDto = coffeechatQueryService.getCoffeechat(request.getCoffeechatId()).getCoffeechat();

        if (!coffeechatDto.getProgressStatus().equals(ProgressStatus.COMPLETE)) {
            throw new BusinessException(ErrorCode.INVALID_COFFEECHAT_STATUS_COMPLETE);
        }

        if (!coffeechatDto.getMenteeId().equals(userId)) {
            throw new BusinessException(ErrorCode.REVIEW_CREATE_NOT_ALLOWED);
        }

        if (reviewRepository.findByCoffeechatId(request.getCoffeechatId()).isPresent()) {
            throw new BusinessException(ErrorCode.REVIEW_ALREADY_EXIST);
        }

        Review review = Review.of(
                request.getRating(),
                request.getComment(),
                request.getTip(),
                request.getCoffeechatId(),
                userId
        );

        return reviewRepository.save(review);
    }
}