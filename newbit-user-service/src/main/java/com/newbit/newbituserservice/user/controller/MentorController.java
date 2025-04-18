package com.newbit.newbituserservice.mentor.controller;


import com.newbit.newbituserservice.common.dto.ApiResponse;
import com.newbit.newbituserservice.user.dto.response.MentorDTO;
import com.newbit.newbituserservice.user.service.MentorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mentor")
@RequiredArgsConstructor
public class MentorController {

    private final MentorService mentorService;

    // 1. 멘토 정보 조회
    @GetMapping("/{mentorId}")
    public ResponseEntity<ApiResponse<MentorDTO>> getMentorInfo(@PathVariable Long mentorId) {
        MentorDTO mentorInfo = mentorService.getMentorInfo(mentorId);
        return ResponseEntity.ok(ApiResponse.success(mentorInfo));
    }

    // 2. 멘토 생성 (멘토 권한 부여 포함)
    @PostMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> createMentor(@PathVariable Long userId) {
        mentorService.createMentor(userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // 3. mentorId로 userId 조회
    @GetMapping("/{mentorId}/user-id")
    public ResponseEntity<ApiResponse<Long>> getUserIdByMentorId(@PathVariable Long mentorId) {
        Long userId = mentorService.getUserIdByMentorId(mentorId);
        return ResponseEntity.ok(ApiResponse.success(userId));
    }

    @GetMapping("user/{userId}/mentor-id")
    public ResponseEntity<ApiResponse<Long>> getMentorIdByUserId(@PathVariable("userId") Long userId) {
        Long mentorId = mentorService.getMentorIdByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(mentorId));
    }
}