package com.newbit.newbituserservice.client;

import com.newbit.newbituserservice.common.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "newbit-feature-service", contextId = "reportServiceClient", configuration = FeignClientConfig.class)
public interface ReportServiceClient {

    @GetMapping("/internal/reports/report-count/{userId}")
    int getTotalReportCountByUserId(@PathVariable("userId") Long userId);
}