package com.newbit.column.controller;

import com.newbit.column.dto.request.CreateColumnRequestDto;
import com.newbit.column.dto.request.DeleteColumnRequestDto;
import com.newbit.column.dto.request.UpdateColumnRequestDto;
import com.newbit.column.dto.response.CreateColumnResponseDto;
import com.newbit.column.dto.response.DeleteColumnResponseDto;
import com.newbit.column.dto.response.UpdateColumnResponseDto;
import com.newbit.column.service.ColumnRequestService;
import com.newbit.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/columns/requests")
@RequiredArgsConstructor
public class ColumnRequestController {
    private final ColumnRequestService columnRequestService;

    // 칼럼 등록 요청 API
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CreateColumnResponseDto> createColumnRequest(
            @RequestBody @Valid CreateColumnRequestDto dto,
            @RequestParam Long mentorId // 프론트에서 mentorId 넘겨준다고 가정
    ) {
        return ApiResponse.success(columnRequestService.createColumnRequest(dto, mentorId));
    }

    // 칼럼 수정 요청 API
    @PostMapping("/{columnId}/edit")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<UpdateColumnResponseDto> updateColumnRequest(
            @PathVariable Long columnId,
            @RequestBody @Valid UpdateColumnRequestDto dto
            ) {
        return ApiResponse.success(columnRequestService.updateColumnRequest(dto, columnId));
    }

    // 칼럼 삭제 요청 API
    @PostMapping("/{columnId}/delete")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<DeleteColumnResponseDto> deleteColumnRequest(
            @PathVariable Long columnId,
            @RequestBody @Valid DeleteColumnRequestDto dto
            ) {
        return ApiResponse.success(columnRequestService.deleteColumnRequest(dto, columnId));

    }
}
