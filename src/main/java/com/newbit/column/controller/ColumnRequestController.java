package com.newbit.column.controller;

import com.newbit.column.dto.request.CreateColumnRequestDto;
import com.newbit.column.dto.response.CreateColumnResponseDto;
import com.newbit.column.service.ColumnRequestService;
import com.newbit.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/columns/requests")
@RequiredArgsConstructor
public class ColumnRequestController {
    private final ColumnRequestService columnRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CreateColumnResponseDto> createColumnRequest(
            @RequestBody @Valid CreateColumnRequestDto dto,
            @RequestParam Long mentorId // 프론트에서 mentorId 넘겨준다고 가정
    ) {
        return ApiResponse.success(columnRequestService.createColumnRequest(dto, mentorId));
    }
}
