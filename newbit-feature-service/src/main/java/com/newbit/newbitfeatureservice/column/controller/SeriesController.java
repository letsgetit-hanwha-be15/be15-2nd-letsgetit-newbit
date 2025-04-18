package com.newbit.newbitfeatureservice.column.controller;

import com.newbit.newbitfeatureservice.security.model.CustomUser;
import com.newbit.newbitfeatureservice.column.dto.request.CreateSeriesRequestDto;
import com.newbit.newbitfeatureservice.column.dto.request.UpdateSeriesRequestDto;
import com.newbit.newbitfeatureservice.column.dto.response.*;
import com.newbit.newbitfeatureservice.column.service.SeriesService;
import com.newbit.newbitfeatureservice.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/series")
@Tag(name = "시리즈 API", description = "시리즈 관련 API")
public class SeriesController {

    private final SeriesService seriesService;

    @PostMapping
    @Operation(summary = "시리즈 생성", description = "기존 칼럼을 묶어 시리즈를 생성합니다.")
    public ApiResponse<CreateSeriesResponseDto> createSeries(
            @RequestBody @Valid CreateSeriesRequestDto dto,
            @AuthenticationPrincipal CustomUser customUser
            ) {
        return ApiResponse.success(seriesService.createSeries(dto, customUser.getUserId()));
    }

    @PatchMapping("/{seriesId}")
    @Operation(summary = "시리즈 수정", description = "기존 시리즈 정보를 수정합니다.")
    public ApiResponse<UpdateSeriesResponseDto> updateSeries(
            @PathVariable Long seriesId,
            @RequestBody @Valid UpdateSeriesRequestDto dto,
            @AuthenticationPrincipal CustomUser customUser
    ) {
        return ApiResponse.success(seriesService.updateSeries(seriesId, dto, customUser.getUserId()));
    }

    @DeleteMapping("/{seriesId}")
    @Operation(summary = "시리즈 삭제", description = "시리즈를 삭제하고, 연결된 칼럼의 시리즈 정보를 제거합니다.")
    public ApiResponse<Void> deleteSeries(
            @PathVariable Long seriesId,
            @AuthenticationPrincipal CustomUser customUser
    ) {
        seriesService.deleteSeries(seriesId, customUser.getUserId());
        return ApiResponse.success(null);
    }

    @GetMapping("/{seriesId}")
    @Operation(summary = "시리즈 상세 조회", description = "시리즈의 상세 정보를 조회합니다.")
    public ApiResponse<GetSeriesDetailResponseDto> getSeriesDetail(
            @PathVariable Long seriesId
    ) {
        return ApiResponse.success(seriesService.getSeriesDetail(seriesId));
    }

    @GetMapping("/my")
    @Operation(summary = "본인 시리즈 목록 조회", description = "멘토 본인이 생성한 시리즈 목록을 조회합니다.")
    public ApiResponse<List<GetMySeriesListResponseDto>> getMySeriesList(
            @AuthenticationPrincipal CustomUser customUser
    ) {
        return ApiResponse.success(seriesService.getMySeriesList(customUser.getUserId()));
    }

    @GetMapping("/{seriesId}/columns")
    @Operation(summary = "시리즈에 포함된 칼럼 목록 조회", description = "해당 시리즈에 포함된 칼럼 목록을 조회합니다.")
    public ApiResponse<List<GetSeriesColumnsResponseDto>> getSeriesColumns(
            @PathVariable Long seriesId
    ) {
        return ApiResponse.success(seriesService.getSeriesColumns(seriesId));
    }

}
