package com.newbit.user.controller;

import com.newbit.common.dto.ApiResponse;
import com.newbit.user.dto.request.ChangePasswordRequestDTO;
import com.newbit.user.dto.request.DeleteUserRequestDTO;
import com.newbit.user.dto.request.UserInfoUpdateRequestDTO;
import com.newbit.user.dto.response.UserDTO;
import com.newbit.user.service.UserInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User", description = "정보 조회 (나의 프로필, 다른 사용자 프로필)")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserInfoController {

    private final UserInfoService userInfoService;

    @Operation(summary = "회원 정보 조회", description = "내 프로필 조회")
    @GetMapping("/myprofile")
    public ResponseEntity<ApiResponse<UserDTO>> getMyInfo(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername(); // 현재 로그인한 사용자의 이메일
        UserDTO myInfo = userInfoService.getMyInfo(email); // 서비스로 위임
        return ResponseEntity.ok(ApiResponse.success(myInfo));
    }

    @Operation(summary = "회원 정보 수정", description = "내 프로필 정보 수정")
    @PutMapping("/myprofile-modify")
    public ResponseEntity<ApiResponse<UserDTO>> updateMyInfo(@RequestBody @Valid UserInfoUpdateRequestDTO request) {
        UserDTO updatedInfo = userInfoService.updateMyInfo(request);
        return ResponseEntity.ok(ApiResponse.success(updatedInfo));
    }

    @Operation(summary = "비밀번호 변경", description = "비밀번호 수정")
    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@RequestBody ChangePasswordRequestDTO request) {
        userInfoService.changePassword(request.getCurrentPassword(), request.getNewPassword());
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "회원 탈퇴", description = "비밀번호 확인 후 회원 탈퇴")
    @DeleteMapping("/unsubscribe")
    public ResponseEntity<ApiResponse<String>> deleteUser(@RequestBody @Valid DeleteUserRequestDTO request) {
        userInfoService.unsubscribeService(request);
        return ResponseEntity.ok(ApiResponse.success("회원 탈퇴가 완료되었습니다."));
    }

}

