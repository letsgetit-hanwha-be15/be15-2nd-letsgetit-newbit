package com.newbit.coffeechat.query;

import com.newbit.coffeechat.query.dto.request.CoffeechatSearchRequest;
import com.newbit.coffeechat.query.dto.response.CoffeechatDetailResponse;
import com.newbit.coffeechat.query.dto.response.CoffeechatListResponse;
import com.newbit.coffeechat.query.service.CoffeechatQueryService;
import com.newbit.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
            summary = "커피챗 목록 조회", description = "커피챗 목록 정보를 조회한다."
    )
    @GetMapping({"/", ""})
    public ResponseEntity<ApiResponse<CoffeechatListResponse>> getCoffeechats(
            CoffeechatSearchRequest coffeechatSearchRequest
    ) {

        // TODO : 로그인한 회원 정보 읽어오기

        // TODO : 회원이 멘토인지 확인하고 멘토이면 멘토변수에 멘토ID, 멘티이면 멘티변수에 회원ID를 넣어서 coffeechatSearchReqeust를 가공해서 넣는다.

        CoffeechatListResponse response = coffeechatQueryService.getCoffeechats(coffeechatSearchRequest);

        return ResponseEntity.ok(ApiResponse.success(response));

    }

    @Operation(
            summary = "커피챗별 신청시간 목록 조회", description = "커피챗별 신청시간 목록 정보를 조회한다."
    )
    @GetMapping("/{coffeechatId}/request-times")
    public ResponseEntity<ApiResponse<CoffeechatListResponse>> getCoffeechatRequestTimes(
            @PathVariable Long coffeechatId
    ) {

        // TODO : 로그인한 회원 정보 읽어오기

        // TODO : 회원이 멘토인지 확인하고 멘토가 아니면 에러를 발생시킨다.

        CoffeechatListResponse response = coffeechatQueryService.getCoffeechatRequestTimes(coffeechatId);

        return ResponseEntity.ok(ApiResponse.success(response));

    }
}
