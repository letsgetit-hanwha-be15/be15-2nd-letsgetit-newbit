package com.newbit.purchase.query.service;


import com.newbit.common.dto.Pagination;
import com.newbit.purchase.query.dto.request.HistoryRequest;
import com.newbit.purchase.query.dto.response.AssetHistoryDto;
import com.newbit.purchase.query.dto.response.AssetHistoryListResponse;
import com.newbit.purchase.query.repository.AssetHistoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PointHistoryQueryService {

    private final AssetHistoryMapper pointHistoryMapper;

    @Transactional(readOnly = true)
    public AssetHistoryListResponse getPointHistories(HistoryRequest request) {

        List<AssetHistoryDto> pointHistories = pointHistoryMapper.findPointHistories(request);
        long totalItems = pointHistoryMapper.countPointHistories(request);

        int page = request.getPage();
        int size = request.getSize();

        return AssetHistoryListResponse.builder()
                .histories(pointHistories)
                .pagination(Pagination.builder()
                        .currentPage(page)
                        .totalPage((int)Math.ceil((double) totalItems / size))
                        .totalItems(totalItems)
                        .build()
                ).build();
    }
}
