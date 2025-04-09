package com.newbit.column.controller;

import com.newbit.column.dto.response.GetColumnDetailResponseDto;
import com.newbit.column.service.ColumnService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/columns")
@RequiredArgsConstructor
@Tag(name = "Column", description = "공개된 컬럼 조회 관련 API")
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
}
