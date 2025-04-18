package com.newbit.newbitfeatureservice.column.controller;

import com.newbit.newbitfeatureservice.security.model.CustomUser;
import com.newbit.newbitfeatureservice.column.dto.request.CreateColumnRequestDto;
import com.newbit.newbitfeatureservice.column.dto.request.DeleteColumnRequestDto;
import com.newbit.newbitfeatureservice.column.dto.request.UpdateColumnRequestDto;
import com.newbit.newbitfeatureservice.column.dto.response.CreateColumnResponseDto;
import com.newbit.newbitfeatureservice.column.dto.response.DeleteColumnResponseDto;
import com.newbit.newbitfeatureservice.column.dto.response.GetMyColumnRequestResponseDto;
import com.newbit.newbitfeatureservice.column.dto.response.UpdateColumnResponseDto;
import com.newbit.newbitfeatureservice.column.service.ColumnRequestService;
import com.newbit.newbitfeatureservice.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/columns/requests")
@RequiredArgsConstructor
@Tag(name = "칼럼 요청 API", description = "멘토 칼럼 요청 관련 API")
public class ColumnRequestController {
    private final ColumnRequestService columnRequestService;

    // 칼럼 등록 요청 API
    @Operation(summary = "멘토 칼럼 등록 요청", description = "멘토가 칼럼 등록을 요청합니다.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CreateColumnResponseDto> createColumnRequest(
            @RequestBody @Valid CreateColumnRequestDto dto,
            @AuthenticationPrincipal CustomUser customUser
    ) {
        return ApiResponse.success(columnRequestService.createColumnRequest(dto, customUser.getUserId()));
    }

    // 칼럼 수정 요청 API
    @Operation(summary = "멘토 칼럼 수정 요청", description = "멘토가 기존 칼럼의 수정을 요청합니다.")
    @PostMapping("/{columnId}/edit")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<UpdateColumnResponseDto> updateColumnRequest(
            @PathVariable Long columnId,
            @RequestBody @Valid UpdateColumnRequestDto dto
            ) {
        return ApiResponse.success(columnRequestService.updateColumnRequest(dto, columnId));
    }

    // 칼럼 삭제 요청 API
    @Operation(summary = "멘토 칼럼 삭제 요청", description = "멘토가 칼럼 삭제를 요청합니다.")
    @PostMapping("/{columnId}/delete")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<DeleteColumnResponseDto> deleteColumnRequest(
            @PathVariable Long columnId,
            @RequestBody @Valid DeleteColumnRequestDto dto
            ) {
        return ApiResponse.success(columnRequestService.deleteColumnRequest(dto, columnId));

    }

    // 본인 칼럼 요청 조회
    @Operation(summary = "멘토 본인 칼럼 요청 조회", description = "멘토가 등록, 수정, 삭제 요청한 칼럼 목록을 조회합니다.")
    @GetMapping("/my")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<GetMyColumnRequestResponseDto>> getMyColumnRequests(
            @AuthenticationPrincipal CustomUser customUser
            ) {
        return ApiResponse.success(columnRequestService.getMyColumnRequests(customUser.getUserId()));
    }
}
