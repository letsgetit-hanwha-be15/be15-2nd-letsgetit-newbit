package com.newbit.user.controller;

import com.newbit.common.dto.ApiResponse;
import com.newbit.user.dto.request.ChangePasswordRequestDTO;
import com.newbit.user.dto.request.DeleteUserRequestDTO;
import com.newbit.user.dto.request.MentorListRequestDTO;
import com.newbit.user.dto.request.UserInfoUpdateRequestDTO;
import com.newbit.user.dto.response.MentorListResponseDTO;
import com.newbit.user.dto.response.MentorProfileDTO;
import com.newbit.user.dto.response.OhterUserProfileDTO;
import com.newbit.user.dto.response.UserDTO;
import com.newbit.user.service.UserInfoService;
import com.newbit.user.service.UserQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "User", description = "정보 조회 (나의 프로필, 다른 사용자 프로필)")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserInfoController {

    private final UserInfoService userInfoService;
    private final UserQueryService userQueryService;

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

    @Operation(summary = "다른 사용자 프로필 조회", description = "다른 사용자의 userId를 기반으로 프로필 조회")
    @GetMapping("/profile/{userId}")
    public ResponseEntity<ApiResponse<OhterUserProfileDTO>> getOhterUserProfile(@PathVariable Long userId) {
        OhterUserProfileDTO profile = userQueryService.getOhterUserProfile(userId);
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    @Operation(summary = "멘토 프로필 조회", description = "멘토의 mentorId를 기반으로 멘토 프로필 및 최근 게시물, 칼럼, 시리즈 3개씩 조회")
    @GetMapping("/mentor-profile/{mentorId}")
    public ResponseEntity<ApiResponse<MentorProfileDTO>> getMentorProfile(@PathVariable Long mentorId) {
        MentorProfileDTO profile = userQueryService.getMentorProfile(mentorId);
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    @Operation(summary = "멘토 목록 조회", description = "조건에 따라 멘토 목록을 조회합니다.")
    @GetMapping("/mentor-list")
    public ResponseEntity<ApiResponse<List<MentorListResponseDTO>>> getMentors(MentorListRequestDTO request) {
        List<MentorListResponseDTO> mentors = userQueryService.getMentors(request);
        return ResponseEntity.ok(ApiResponse.success(mentors));
    }

}

