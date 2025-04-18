package com.newbit.newbitfeatureservice.column.controller;

import com.newbit.newbitfeatureservice.column.dto.response.GetColumnListResponseDto;
import com.newbit.newbitfeatureservice.security.model.CustomUser;
import com.newbit.newbitfeatureservice.column.dto.response.GetColumnDetailResponseDto;
import com.newbit.newbitfeatureservice.column.dto.response.GetMyColumnListResponseDto;
import com.newbit.newbitfeatureservice.column.service.ColumnService;
import com.newbit.newbitfeatureservice.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/columns")
@RequiredArgsConstructor
@Tag(name = "칼럼 조회 API", description = "공개된 컬럼 조회 관련 API")
public class ColumnController {

    private final ColumnService columnService;

    @GetMapping("/{columnId}/user/{userId}")
    @Operation(summary = "공개된 칼럼 상세 조회", description = "columnId에 해당하는 칼럼을 구매한 사용자의 상세 정보를 조회합니다.")
    public GetColumnDetailResponseDto getColumnDetail(
            @Parameter(description = "조회할 칼럼 ID", example = "1") @PathVariable Long columnId,
            @Parameter(description = "조회할 유저 ID", example = "10") @PathVariable Long userId
    ) {
        return columnService.getColumnDetail(userId, columnId);
    }

    @GetMapping("/public-list")
    @Operation(summary = "공개된 칼럼 목록 조회 (페이징)", description = "공개된 모든 칼럼을 페이지별로 조회합니다.")
    public Page<GetColumnListResponseDto> getPublicColumnList(
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10") @RequestParam(defaultValue = "10") int size
    ) {
        return columnService.getPublicColumnList(page, size);
    }

    @Operation(summary = "멘토 본인 칼럼 목록 조회", description = "멘토가 승인된 본인의 칼럼 목록을 조회합니다.")
    @GetMapping("/my")
    public ApiResponse<List<GetMyColumnListResponseDto>> getMyColumnList(
            @AuthenticationPrincipal CustomUser customUser
            ) {
        return ApiResponse.success(columnService.getMyColumnList(customUser.getUserId()));
    }
}
