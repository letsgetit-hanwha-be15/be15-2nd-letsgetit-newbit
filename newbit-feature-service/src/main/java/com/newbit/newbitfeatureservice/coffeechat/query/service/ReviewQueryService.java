package com.newbit.newbitfeatureservice.coffeechat.query.service;

import com.newbit.newbitfeatureservice.coffeechat.query.dto.request.ReviewSearchServiceRequest;
import com.newbit.newbitfeatureservice.coffeechat.query.dto.response.ReviewListDto;
import com.newbit.newbitfeatureservice.coffeechat.query.dto.response.ReviewListResponse;
import com.newbit.newbitfeatureservice.coffeechat.query.mapper.ReviewMapper;
import com.newbit.newbitfeatureservice.common.dto.Pagination;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewQueryService {

    private final ReviewMapper reviewMapper;

    @Transactional(readOnly = true)
    public ReviewListResponse getReviews(ReviewSearchServiceRequest reviewSearchServiceRequest) {
        List<ReviewListDto> reviews = reviewMapper.selectReviews(reviewSearchServiceRequest);
        long totalItems = reviewMapper.countReviews(reviewSearchServiceRequest);

        int page = reviewSearchServiceRequest.getPage();
        int size = reviewSearchServiceRequest.getSize();

        return ReviewListResponse.builder()
                .reviews(reviews)
                .pagination(Pagination.builder()
                        .currentPage(page)
                        .totalPage((int) Math.ceil((double) totalItems / size))
                        .totalItems(totalItems)
                        .build())
                .build();
    }
}
