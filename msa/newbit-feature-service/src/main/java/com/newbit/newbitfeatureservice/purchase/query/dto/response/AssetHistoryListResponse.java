package com.newbit.newbitfeatureservice.purchase.query.dto.response;


import com.newbit.newbitfeatureservice.common.dto.Pagination;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AssetHistoryListResponse {
    @Schema(description = "칼럼 구매 내역 리스트")
    private List<AssetHistoryDto> histories;
    @Schema(description = "페이지네이션 정보")
    private final Pagination pagination;
}
