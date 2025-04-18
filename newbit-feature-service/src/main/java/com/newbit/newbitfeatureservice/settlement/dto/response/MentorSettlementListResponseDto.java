package com.newbit.newbitfeatureservice.settlement.dto.response;

import com.newbit.newbitfeatureservice.common.dto.Pagination;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/* 정산 목록 전체 응답용 */
@Getter
@AllArgsConstructor
@Builder
@Schema(description = "멘토 정산 내역 목록 응답 DTO")
public class MentorSettlementListResponseDto {

    @Schema(description = "정산 내역 리스트")
    private List<MentorSettlementSummaryDto> settlements;

    @Schema(description = "페이징 정보")
    private Pagination pagination;
}
