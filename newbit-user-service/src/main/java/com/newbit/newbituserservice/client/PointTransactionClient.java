package com.newbit.newbituserservice.client;

import com.newbit.newbituserservice.common.config.FeignClientConfig;
import com.newbit.newbituserservice.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "newbit-feature-service", contextId = "PointTransactionClient", configuration = FeignClientConfig.class)
public interface PointTransactionClient {
    @PostMapping("/point/type")
    ApiResponse<Void> givePointByType(
            @RequestParam("userId") Long userId,
            @RequestParam("pointTypeName") String pointTypeName,
            @RequestParam(value = "serviceId", required = false) Long serviceId
    );
}
