package com.newbit.newbituserservice.user.controller;

import com.newbit.newbituserservice.common.dto.ApiResponse;
import com.newbit.newbituserservice.user.dto.response.JobDTO;
import com.newbit.newbituserservice.user.dto.response.TechstackDTO;
import com.newbit.newbituserservice.user.service.JobTechstackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "직종 기술스택 조회 API", description = "직종 및 기술스택 조회 API")
@RestController
@RequiredArgsConstructor
public class JobTechStackController {

    private final JobTechstackService jobTechstackService;

    @Operation(summary = "직업 목록 조회", description = "모든 직업 정보를 조회한다")
    @GetMapping("/jobs") // 직접 경로 지정
    public ResponseEntity<ApiResponse<List<JobDTO>>> getJobs() {
        return ResponseEntity.ok(ApiResponse.success(jobTechstackService.getAllJobs()));
    }

    @Operation(summary = "기술 스택 목록 조회", description = "모든 기술 스택 정보를 조회한다")
    @GetMapping("/techstacks")
    public ResponseEntity<ApiResponse<List<TechstackDTO>>> getTechstacks() {
        return ResponseEntity.ok(ApiResponse.success(jobTechstackService.getAllTechstacks()));
    }

}

