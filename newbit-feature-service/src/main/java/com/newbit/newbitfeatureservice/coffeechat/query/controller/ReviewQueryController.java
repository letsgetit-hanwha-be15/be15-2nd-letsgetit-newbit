package com.newbit.newbitfeatureservice.coffeechat.query.controller;

import com.newbit.newbitfeatureservice.client.user.MentorFeignClient;
import com.newbit.newbitfeatureservice.security.model.CustomUser;
import com.newbit.newbitfeatureservice.coffeechat.query.dto.request.ReviewSearchServiceRequest;
import com.newbit.newbitfeatureservice.coffeechat.query.dto.response.ReviewListResponse;
import com.newbit.newbitfeatureservice.coffeechat.query.service.ReviewQueryService;
import com.newbit.newbitfeatureservice.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "리뷰API", description = "리뷰 조회 API")
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewQueryController {
    private final ReviewQueryService reviewQueryService;
    private final MentorFeignClient mentorClient;

    @Operation(
            summary = "멘토의 리뷰 목록 조회", description = "멘토에게 달린 리뷰 목록을 조회한다."
    )
    @GetMapping("/mentors/{mentorId}")
    public ResponseEntity<ApiResponse<ReviewListResponse>> getMentorReviews(
            @PathVariable Long mentorId
    ) {
        ReviewSearchServiceRequest reviewSearchServiceRequest = new ReviewSearchServiceRequest();
        reviewSearchServiceRequest.setMentorId(mentorId);

        ReviewListResponse response = reviewQueryService.getReviews(reviewSearchServiceRequest);

        return ResponseEntity.ok(ApiResponse.success(response));

    }

    @Operation(
            summary = "내 리뷰 목록 조회", description = "멘티입장에서 내가 작성한 리뷰 목록을 조회한다."
    )
    @GetMapping("/me/written")
    public ResponseEntity<ApiResponse<ReviewListResponse>> getMenteeMyReviews(
            @AuthenticationPrincipal CustomUser customUser
    ) {

        ReviewSearchServiceRequest reviewSearchServiceRequest = new ReviewSearchServiceRequest();
        reviewSearchServiceRequest.setMenteeId(customUser.getUserId());

        ReviewListResponse response = reviewQueryService.getReviews(reviewSearchServiceRequest);

        return ResponseEntity.ok(ApiResponse.success(response));

    }

    @Operation(
            summary = "나에게 달린 리뷰 목록 조회", description = "멘토입장에서 나에게 달린 리뷰 목록을 조회한다."
    )
    @GetMapping("/me/received")
//    @PreAuthorize("hasAuthority('MENTOR')")
    public ResponseEntity<ApiResponse<ReviewListResponse>> getMentorMyReviews(
            @AuthenticationPrincipal CustomUser customUser,
            @RequestParam(defaultValue = "0") int page, // 기본값 0
            @RequestParam(defaultValue = "10") int size // 기본값 10
    ) {

        // 유저 아이디로 멘토 아이디를 찾아오기
        Long userId = customUser.getUserId();
        Long mentorId = mentorClient.getMentorIdByUserId(userId).getData();

        ReviewSearchServiceRequest reviewSearchServiceRequest = new ReviewSearchServiceRequest();
        reviewSearchServiceRequest.setMentorId(mentorId);
        reviewSearchServiceRequest.setPage(page);
        reviewSearchServiceRequest.setSize(size);

        ReviewListResponse response = reviewQueryService.getReviews(reviewSearchServiceRequest);

        return ResponseEntity.ok(ApiResponse.success(response));

    }

}
