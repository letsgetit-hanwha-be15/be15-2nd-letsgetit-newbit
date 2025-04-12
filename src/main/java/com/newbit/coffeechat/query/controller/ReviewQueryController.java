package com.newbit.coffeechat.query.controller;

import com.newbit.coffeechat.query.dto.request.ReviewSearchServiceRequest;
import com.newbit.coffeechat.query.dto.response.CoffeechatDetailResponse;
import com.newbit.coffeechat.query.dto.response.ReviewListResponse;
import com.newbit.coffeechat.query.service.ReviewQueryService;
import com.newbit.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "리뷰API", description = "리뷰 조회 API")
@RestController
@RequestMapping("/api/v1/review")
@RequiredArgsConstructor
public class ReviewQueryController {
    private final ReviewQueryService reviewQueryService;

    @Operation(
            summary = "멘토의 리뷰 목록 조회", description = "멘토에게 달린 리뷰 목록을 조회한다."
    )
    @GetMapping("/{mentorId}")
    public ResponseEntity<ApiResponse<ReviewListResponse>> getCoffeechat(
            @PathVariable Long mentorId
    ) {

        // 서비스 레이어에 보낼 request 생성
        /*
        * request 정보
        * 멘토꺼 리뷰 목록을 조회할지, 멘티가 작성한 리뷰 목록을 조회할지
        * request에는 멘토 / 멘티 둘 중 한 정보만 있고,
        * response 에는 두 정보 다 존재.
        * */
        ReviewSearchServiceRequest reviewSearchServiceRequest = new ReviewSearchServiceRequest();
        reviewSearchServiceRequest.setMentorId(mentorId);

        ReviewListResponse response = reviewQueryService.getReviews(reviewSearchServiceRequest);

        return ResponseEntity.ok(ApiResponse.success(response));

    }

}
