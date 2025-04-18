package com.newbit.newbitfeatureservice.client.user;

import com.newbit.newbitfeatureservice.common.config.feign.UserFeignClientConfig;
import com.newbit.newbitfeatureservice.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "newbit-user-service", contextId = "mailFeignClient", configuration = UserFeignClientConfig.class)
public interface MailFeignClient {

    @PostMapping("/internal/mail/send")
    ApiResponse<Void> sendMail(
            @RequestParam("to") String to,
            @RequestParam("subject") String subject,
            @RequestParam("content") String content
    );
}