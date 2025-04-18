package com.newbit.newbitfeatureservice.client.user;

import com.newbit.newbitfeatureservice.client.user.dto.UserDTO;
import com.newbit.newbitfeatureservice.common.config.feign.UserFeignClientConfig;
import com.newbit.newbitfeatureservice.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "newbit-user-service", contextId = "userFeignClient", configuration = UserFeignClientConfig.class)
public interface UserFeignClient {

    @GetMapping("/user/{userId}")
    ApiResponse<UserDTO> getUserByUserId(@PathVariable("userId") Long userId);

    @GetMapping("/{userId}/email")
    ApiResponse<String> getEmailByUserId(@PathVariable("userId") Long userId);

    @GetMapping("/{userId}/nickname")
    ApiResponse<String> getNicknameByUserId(@PathVariable("userId") Long userId);
}