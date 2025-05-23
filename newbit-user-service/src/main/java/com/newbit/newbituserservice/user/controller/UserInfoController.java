package com.newbit.newbituserservice.user.controller;

import com.newbit.newbituserservice.common.dto.ApiResponse;
import com.newbit.newbituserservice.security.model.CustomUser;
import com.newbit.newbituserservice.user.dto.request.*;
import com.newbit.newbituserservice.user.dto.response.*;
import com.newbit.newbituserservice.user.service.UserInfoService;
import com.newbit.newbituserservice.user.service.UserQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "유저 정보 API", description = "정보 조회 (나의 프로필, 다른 사용자 프로필)")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Slf4j
public class UserInfoController {

    private final UserInfoService userInfoService;
    private final UserQueryService userQueryService;

    @Operation(summary = "회원 정보 조회", description = "내 프로필 조회")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDTO>> getMyInfo(@AuthenticationPrincipal CustomUser user) {// 현재 로그인한 사용자의 이메일
        UserDTO myInfo = userInfoService.getMyInfo(user.getUserId()); // 서비스로 위임
        return ResponseEntity.ok(ApiResponse.success(myInfo));
    }

    @Operation(summary = "회원 프로필 정보 수정", description = "내 프로필 정보 수정")
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<Void>> updateMyProfileInfo(
            @RequestBody @Valid UserProfileInfoUpdateRequestDTO request,
            @AuthenticationPrincipal CustomUser customUser) {

        userInfoService.updateMyProfileInfo(request, customUser.getUserId());
        return ResponseEntity.ok(ApiResponse.success(null));
    }


    @Operation(summary = "회원 개인정보 수정", description = "개인 정보 수정")
    @PutMapping("/me/info")
    public ResponseEntity<ApiResponse<Void>> updateMyInfo(
            @RequestBody @Valid UserInfoUpdateRequestDTO request,
            @AuthenticationPrincipal CustomUser customUser) {

        userInfoService.updateMyInfo(request, customUser.getUserId());
        return ResponseEntity.ok(ApiResponse.success(null));
    }


    @Operation(summary = "회원 탈퇴", description = "비밀번호 확인 후 회원 탈퇴")
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<String>> deleteUser(@RequestBody @Valid DeleteUserRequestDTO request) {
        userInfoService.unsubscribeService(request);
        return ResponseEntity.ok(ApiResponse.success("회원 탈퇴가 완료되었습니다."));
    }

    @Operation(summary = "다른 사용자 프로필 조회", description = "다른 사용자의 userId를 기반으로 프로필 조회")
    @GetMapping("/{userId}/profile")
    public ResponseEntity<ApiResponse<OhterUserProfileDTO>> getOhterUserProfile(@PathVariable Long userId) {
        OhterUserProfileDTO profile = userQueryService.getOhterUserProfile(userId);
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    @Operation(summary = "멘토 프로필 조회", description = "멘토의 mentorId를 기반으로 멘토 프로필 및 최근 게시물, 칼럼, 시리즈 3개씩 조회")
    @GetMapping("/mentor/{mentorId}/profile")
    public ResponseEntity<ApiResponse<MentorProfileDTO>> getMentorProfile(@PathVariable Long mentorId) {
        MentorProfileDTO profile = userQueryService.getMentorProfile(mentorId);
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    @Operation(summary = "멘토 목록 조회", description = "조건에 따라 멘토 목록을 조회합니다.")
    @GetMapping("/mentors")
    public ResponseEntity<ApiResponse<MentorListResponseWrapper>> getMentors(MentorListRequestDTO request) {
        MentorListResponseWrapper response = userQueryService.getMentors(request);
        return ResponseEntity.ok(ApiResponse.success(response));
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

    @Operation(summary = "멘토 커피챗 정보 수정", description = "멘토 커피쳇 정보 수정")
    @PatchMapping("/me/coffeechat-info")
    public ResponseEntity<ApiResponse<Void>> updateMentorCoffeechatInfo(
            @AuthenticationPrincipal CustomUser customUser,
            @RequestBody MentorCoffeechatInfoDTO request) {

        userInfoService.updateMentorCoffeechatInfo(request, customUser.getUserId());
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "멘토 소개 정보 수정", description = "멘토 소개 정보 수정")
    @PatchMapping("/me/introduction-info")
    public ResponseEntity<ApiResponse<Void>> updateMentorIntroductionInfo(
            @AuthenticationPrincipal CustomUser customUser,
            @RequestBody MentorIntroduceInfoDTO request) {

        userInfoService.updateMentorIntroductionInfo(request, customUser.getUserId());
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "포인트, 다이아 잔여량 조회", description = "포인트, 다이아 잔여량 조회")
    @GetMapping("/me/balance")
    public ResponseEntity<ApiResponse<BalanceDTO>> getBalance(@AuthenticationPrincipal CustomUser customUser) {
        BalanceDTO response = userInfoService.getUserBalance(customUser.getUserId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}

