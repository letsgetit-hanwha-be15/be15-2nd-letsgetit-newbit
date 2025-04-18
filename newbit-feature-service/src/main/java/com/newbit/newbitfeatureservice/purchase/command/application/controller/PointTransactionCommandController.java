package com.newbit.newbitfeatureservice.purchase.command.application.controller;

import com.newbit.newbitfeatureservice.common.dto.ApiResponse;
import com.newbit.newbitfeatureservice.purchase.command.application.service.PointTransactionCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/point")
public class PointTransactionCommandController {

    private final PointTransactionCommandService pointTransactionCommandService;

    @PostMapping("/type")
    public ResponseEntity<ApiResponse<Void>> givePointByType(
            @RequestParam Long userId,
            @RequestParam String pointTypeName,
            @RequestParam(required = false) Long serviceId
    ) {
        log.info("givePointByType: userId={}, pointTypeName={}", userId, pointTypeName);
        pointTransactionCommandService.givePointByType(userId, pointTypeName, serviceId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
