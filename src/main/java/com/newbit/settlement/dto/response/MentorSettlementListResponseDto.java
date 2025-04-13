package com.newbit.settlement.dto.response;

import com.newbit.common.dto.Pagination;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class MentorSettlementListResponseDto {
    private List<MentorSettlementSummaryDto> settlements;
    private Pagination pagination;
}
