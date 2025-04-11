package com.newbit.user.controller;

import com.newbit.user.dto.response.UserDTO;
import com.newbit.user.service.UserInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User", description = "정보 조회 (나의 프로필, 다른 사용자 프로필)")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserInfoController {

    private final UserInfoService userInfoService;

    @Operation(summary = "회원 정보 조회", description = "회원 정보 조회 기능")
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getMyInfo(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername(); // 현재 로그인한 사용자의 이메일
        UserDTO myInfo = userInfoService.getMyInfo(email); // 서비스로 위임
        return ResponseEntity.ok(myInfo); // 사용자 정보 반환
    }
}

