package com.newbit.newbitfeatureservice.coffeechat.query.controller;

import com.newbit.newbitfeatureservice.client.user.MentorFeignClient;
import com.newbit.newbitfeatureservice.security.model.CustomUser;
import com.newbit.newbitfeatureservice.coffeechat.command.domain.aggregate.ProgressStatus;
import com.newbit.newbitfeatureservice.coffeechat.query.dto.request.CoffeechatSearchServiceRequest;
import com.newbit.newbitfeatureservice.coffeechat.query.dto.response.CoffeechatDetailResponse;
import com.newbit.newbitfeatureservice.coffeechat.query.dto.response.CoffeechatListResponse;
import com.newbit.newbitfeatureservice.coffeechat.query.dto.response.RequestTimeListResponse;
import com.newbit.newbitfeatureservice.coffeechat.query.service.CoffeechatQueryService;
import com.newbit.newbitfeatureservice.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "커피챗 API", description = "커피챗 조회 API")
@RestController
@RequestMapping("/coffeechats")
@RequiredArgsConstructor
public class CoffeechatQueryController {
    private final CoffeechatQueryService coffeechatQueryService;
    private final MentorFeignClient mentorClient;

    @Operation(
            summary = "커피챗 상세 조회", description = "커피챗 상세 정보를 조회한다."
    )
    @GetMapping("/{coffeechatId}")
    public ResponseEntity<ApiResponse<CoffeechatDetailResponse>> getCoffeechat(
            @PathVariable Long coffeechatId
    ) {

        CoffeechatDetailResponse response = coffeechatQueryService.getCoffeechat(coffeechatId);

        return ResponseEntity.ok(ApiResponse.success(response));

    }

    @Operation(
            summary = "멘토의 커피챗 목록 조회", description = "멘토ID로 커피챗 목록 정보를 조회한다."
    )
    @GetMapping({"/mentor"})
//    @PreAuthorize("hasAuthority('MENTOR')")
    public ResponseEntity<ApiResponse<CoffeechatListResponse>> getMentorCoffeechats(
            @AuthenticationPrincipal CustomUser customUser
    ) {

        // 유저 아이디로 멘토 아이디를 찾아오기
        Long userId = customUser.getUserId();
        Long mentorId = mentorClient.getMentorIdByUserId(userId).getData();

        // 서비스 레이어에 보낼 request 생성
        CoffeechatSearchServiceRequest coffeechatSearchServiceRequest = new CoffeechatSearchServiceRequest();

        coffeechatSearchServiceRequest.setMentorId(mentorId);

        CoffeechatListResponse response = coffeechatQueryService.getCoffeechats(coffeechatSearchServiceRequest);

        return ResponseEntity.ok(ApiResponse.success(response));

    }

    @Operation(
            summary = "멘토의 커피챗 요청 목록 조회", description = "멘토ID로 커피챗 요청(IN_PROGRESSING) 목록 정보를 조회한다."
    )
    @GetMapping({"/mentor/in-progress"})
//    @PreAuthorize("hasAuthority('MENTOR')")
    public ResponseEntity<ApiResponse<CoffeechatListResponse>> getMentorInProgressCoffeechats(
            @AuthenticationPrincipal CustomUser customUser
    ) {
        // 유저 아이디로 멘토 아이디를 찾아오기
        Long userId = customUser.getUserId();
        Long mentorId = mentorClient.getMentorIdByUserId(userId).getData();

        // 서비스 레이어에 보낼 request 생성
        CoffeechatSearchServiceRequest coffeechatSearchServiceRequest = new CoffeechatSearchServiceRequest();
        coffeechatSearchServiceRequest.setMentorId(mentorId);
        coffeechatSearchServiceRequest.setProgressStatus(ProgressStatus.IN_PROGRESS);

        CoffeechatListResponse response = coffeechatQueryService.getCoffeechats(coffeechatSearchServiceRequest);

        return ResponseEntity.ok(ApiResponse.success(response));

    }

    @Operation(
            summary = "멘티의 커피챗 목록 조회", description = "멘티ID로 커피챗 목록 정보를 조회한다."
    )
    @GetMapping({"/mentee"})
    public ResponseEntity<ApiResponse<CoffeechatListResponse>> getMenteeCoffeechats(
            @AuthenticationPrincipal CustomUser customUser
    ) {

        // 서비스 레이어에 보낼 request 생성
        CoffeechatSearchServiceRequest coffeechatSearchServiceRequest = new CoffeechatSearchServiceRequest();
        Long menteeId = customUser.getUserId();
        coffeechatSearchServiceRequest.setMenteeId(menteeId);

        CoffeechatListResponse response = coffeechatQueryService.getCoffeechats(coffeechatSearchServiceRequest);

        return ResponseEntity.ok(ApiResponse.success(response));

    }

    @Operation(
            summary = "커피챗별 신청시간 목록 조회", description = "커피챗별 신청시간 목록 정보를 조회한다."
    )
    @GetMapping("/{coffeechatId}/request-times")
    public ResponseEntity<ApiResponse<RequestTimeListResponse>> getCoffeechatRequestTimes(
            @PathVariable Long coffeechatId
    ) {

        // TODO : 로그인한 회원 정보 읽어오기

        RequestTimeListResponse response = coffeechatQueryService.getCoffeechatRequestTimes(coffeechatId);

        return ResponseEntity.ok(ApiResponse.success(response));

    }
}
