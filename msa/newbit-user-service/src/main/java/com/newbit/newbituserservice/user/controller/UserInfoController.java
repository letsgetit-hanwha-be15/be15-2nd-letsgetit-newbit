package com.newbit.newbituserservice.user.controller;

import com.newbit.newbituserservice.common.dto.ApiResponse;
import com.newbit.newbituserservice.security.model.CustomUser;
import com.newbit.newbituserservice.user.dto.request.ChangePasswordRequestDTO;
import com.newbit.newbituserservice.user.dto.request.DeleteUserRequestDTO;
import com.newbit.newbituserservice.user.dto.request.MentorListRequestDTO;
import com.newbit.newbituserservice.user.dto.request.UserInfoUpdateRequestDTO;
import com.newbit.newbituserservice.user.dto.response.MentorListResponseDTO;
import com.newbit.newbituserservice.user.dto.response.MentorProfileDTO;
import com.newbit.newbituserservice.user.dto.response.OhterUserProfileDTO;
import com.newbit.newbituserservice.user.dto.response.UserDTO;
import com.newbit.newbituserservice.user.service.UserInfoService;
import com.newbit.newbituserservice.user.service.UserQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "유저 정보 API", description = "정보 조회 (나의 프로필, 다른 사용자 프로필)")
@RestController
@RequiredArgsConstructor
@Slf4j
public class UserInfoController {

    private final UserInfoService userInfoService;
    private final UserQueryService userQueryService;

    @Operation(summary = "회원 정보 조회", description = "내 프로필 조회")
    @GetMapping("/myprofile")
    public ResponseEntity<ApiResponse<UserDTO>> getMyInfo(@AuthenticationPrincipal CustomUser user) {// 현재 로그인한 사용자의 이메일
        UserDTO myInfo = userInfoService.getMyInfo(user.getUserId()); // 서비스로 위임
        return ResponseEntity.ok(ApiResponse.success(myInfo));
    }

    @Operation(summary = "회원 정보 수정", description = "내 프로필 정보 수정")
    @PutMapping("/myprofile-modify")
    public ResponseEntity<ApiResponse<UserDTO>> updateMyInfo(@RequestBody @Valid UserInfoUpdateRequestDTO request
    , @AuthenticationPrincipal CustomUser customUser) {
        UserDTO updatedInfo = userInfoService.updateMyInfo(request, customUser.getUserId());
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


    @GetMapping("/{userId}/email")
    public ResponseEntity<ApiResponse<String>> getEmailByUserId(@PathVariable Long userId) {
        String email = userQueryService.getEmailByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(email));
    }

    @GetMapping("/{userId}/nickname")
    public ResponseEntity<ApiResponse<String>> getNicknameByUserId(@PathVariable Long userId) {
        String nickname = userQueryService.getNicknameByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(nickname));
    }

}

