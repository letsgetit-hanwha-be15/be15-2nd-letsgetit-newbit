package com.newbit.newbitfeatureservice.coffeechat.query.service;

import com.newbit.newbitfeatureservice.coffeechat.query.dto.request.ReviewSearchServiceRequest;
import com.newbit.newbitfeatureservice.coffeechat.query.dto.response.ReviewListDto;
import com.newbit.newbitfeatureservice.coffeechat.query.dto.response.ReviewListResponse;
import com.newbit.newbitfeatureservice.coffeechat.query.mapper.ReviewMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewQueryServiceTest {
    @Mock
    private ReviewMapper reviewMapper;

    @InjectMocks
    private ReviewQueryService reviewQueryService;

    @DisplayName("멘토에게 달린 리뷰 목록 조회 성공")
    @Test
    void getMentorReviews_성공() {
        // given
        Long mentorId = 1L;
        ReviewSearchServiceRequest reviewSearchServiceRequest = new ReviewSearchServiceRequest();
        reviewSearchServiceRequest.setMentorId(mentorId);

        ReviewListDto review1 = new ReviewListDto(1L, BigDecimal.valueOf(5L), "최고였어요", 4L, "멘티1");
        ReviewListDto review2 = new ReviewListDto(2L, BigDecimal.valueOf(3L), "그냥 그랬어요", 5L, "멘티2");

        List<ReviewListDto> originalList = Arrays.asList(review1, review2);
        when(reviewMapper.selectReviews(reviewSearchServiceRequest)).thenReturn(originalList);

        // when
        ReviewListResponse results = reviewQueryService.getReviews(reviewSearchServiceRequest);

        // then

        // 주어진 결과 값이 올바른 비즈니스 로직을 통해 가공되었는지 확인
        assertNotNull(results);
        assertEquals(2, results.getReviews().size());
        assertEquals("최고였어요", results.getReviews().get(0).getComment());
        assertEquals("그냥 그랬어요", results.getReviews().get(1).getComment());
        // 해당 객체에서 메소드 호출 여부 확인 -> 서비스 내부의 상호 작용이 기대한 대로 이루어졌는지
        verify(reviewMapper).selectReviews(reviewSearchServiceRequest);
        verify(reviewMapper).countReviews(reviewSearchServiceRequest);
    }

    @DisplayName("멘티가 본인이 작성한 리뷰 목록 조회 성공")
    @Test
    void getMenteeMyReviews_성공() {
        // given
        Long menteeId = 4L;
        ReviewSearchServiceRequest reviewSearchServiceRequest = new ReviewSearchServiceRequest();
        reviewSearchServiceRequest.setMenteeId(menteeId);

        ReviewListDto review1 = new ReviewListDto(1L, BigDecimal.valueOf(5L), "최고였어요", menteeId, "멘티1");

        List<ReviewListDto> originalList = Arrays.asList(review1);
        when(reviewMapper.selectReviews(reviewSearchServiceRequest)).thenReturn(originalList);

        // when
        ReviewListResponse results = reviewQueryService.getReviews(reviewSearchServiceRequest);

        // then

        // 주어진 결과 값이 올바른 비즈니스 로직을 통해 가공되었는지 확인
        assertNotNull(results);
        assertEquals(1, results.getReviews().size());
        assertEquals("최고였어요", results.getReviews().get(0).getComment());
        // 해당 객체에서 메소드 호출 여부 확인 -> 서비스 내부의 상호 작용이 기대한 대로 이루어졌는지
        verify(reviewMapper).selectReviews(reviewSearchServiceRequest);
        verify(reviewMapper).countReviews(reviewSearchServiceRequest);
    }
}