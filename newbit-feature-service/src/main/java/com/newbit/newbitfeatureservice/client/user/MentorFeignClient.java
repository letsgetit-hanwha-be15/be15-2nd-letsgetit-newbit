package com.newbit.newbitfeatureservice.client.user;

import com.newbit.newbitfeatureservice.client.user.dto.MentorDTO;

import com.newbit.newbitfeatureservice.common.config.feign.UserFeignClientConfig;
import com.newbit.newbitfeatureservice.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "newbit-user-service", contextId = "mentorFeignClient", configuration = UserFeignClientConfig.class)
public interface MentorFeignClient {

    @GetMapping("/mentor/{mentorId}")
    ApiResponse<MentorDTO> getMentorInfo(@PathVariable("mentorId") Long mentorId);

    @PostMapping("/mentor/{userId}")
    ApiResponse<Void> createMentor(@PathVariable("userId") Long userId);

    @GetMapping("/mentor/{mentorId}/user-id")
    ApiResponse<Long> getUserIdByMentorId(@PathVariable("mentorId") Long mentorId);

    @GetMapping("/mentor/user/{userId}/mentor-id")
    ApiResponse<Long> getMentorIdByUserId(@PathVariable("userId") Long userId);
}