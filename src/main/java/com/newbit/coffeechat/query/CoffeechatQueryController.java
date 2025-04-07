package com.newbit.coffeechat.query;

import com.newbit.coffeechat.query.dto.response.CoffeechatDetailResponse;
import com.newbit.coffeechat.query.service.CoffeechatQueryService;
import com.newbit.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CoffeechatQueryController {
    private final CoffeechatQueryService coffeechatQueryService;

    @GetMapping("/coffeechats/{coffeechatId}")
    public ResponseEntity<ApiResponse<CoffeechatDetailResponse>> getCoffeechat(
            @PathVariable Long coffeechatId
    ) {

        CoffeechatDetailResponse response = coffeechatQueryService.getCoffeechat(coffeechatId);

        return ResponseEntity.ok(ApiResponse.success(response));

    }
}
