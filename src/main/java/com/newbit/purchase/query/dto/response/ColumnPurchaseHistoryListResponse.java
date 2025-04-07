package com.newbit.purchase.query.dto.response;

import com.newbit.common.dto.Pagination;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ColumnPurchaseHistoryListResponse {
    private List<ColumnPurchaseHistoryDto> columnPurchases;
    private Pagination pagination;
}