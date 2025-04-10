package com.newbit.coffeechat.query;

import com.newbit.auth.model.CustomUser;
import com.newbit.coffeechat.query.dto.request.CoffeechatSearchRequest;
import com.newbit.coffeechat.query.dto.response.CoffeechatDetailResponse;
import com.newbit.coffeechat.query.dto.response.CoffeechatListResponse;
import com.newbit.coffeechat.query.dto.response.RequestTimeListResponse;
import com.newbit.coffeechat.query.service.CoffeechatQueryService;
import com.newbit.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Coffeechat 도메인")
@RestController
@RequestMapping("/api/v1/coffeechats")
@RequiredArgsConstructor
public class CoffeechatQueryController {
    private final CoffeechatQueryService coffeechatQueryService;

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
    @PreAuthorize("hasAuthority('MENTOR')")
    public ResponseEntity<ApiResponse<CoffeechatListResponse>> getMentorCoffeechats(
            CoffeechatSearchRequest coffeechatSearchRequest,
            @AuthenticationPrincipal CustomUser customUser
    ) {

        Long mentorId = customUser.getUserId();
        coffeechatSearchRequest.setMentorId(mentorId);
        coffeechatSearchRequest.setMenteeId(coffeechatSearchRequest.getUserId());

        CoffeechatListResponse response = coffeechatQueryService.getCoffeechats(coffeechatSearchRequest);

        return ResponseEntity.ok(ApiResponse.success(response));

    }

    @Operation(
            summary = "멘티의 커피챗 목록 조회", description = "멘티ID로 커피챗 목록 정보를 조회한다."
    )
    @GetMapping({"/mentee"})
    public ResponseEntity<ApiResponse<CoffeechatListResponse>> getMenteeCoffeechats(
            CoffeechatSearchRequest coffeechatSearchRequest,
            @AuthenticationPrincipal CustomUser customUser
    ) {

        Long menteeId = customUser.getUserId();
        coffeechatSearchRequest.setMenteeId(menteeId);
        coffeechatSearchRequest.setMentorId(coffeechatSearchRequest.getUserId());

        CoffeechatListResponse response = coffeechatQueryService.getCoffeechats(coffeechatSearchRequest);

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
