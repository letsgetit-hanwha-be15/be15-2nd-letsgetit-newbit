package com.newbit.newbitfeatureservice.purchase.query.service;

import com.newbit.newbitfeatureservice.common.dto.Pagination;
import com.newbit.newbitfeatureservice.purchase.query.dto.request.HistoryRequest;
import com.newbit.newbitfeatureservice.purchase.query.dto.response.AssetHistoryDto;
import com.newbit.newbitfeatureservice.purchase.query.dto.response.AssetHistoryListResponse;
import com.newbit.newbitfeatureservice.purchase.query.repository.AssetHistoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DiamondHistoryQueryService {

    private final AssetHistoryMapper diamondHistoryMapper;

    @Transactional(readOnly = true)
    public AssetHistoryListResponse getDiamondHistories(HistoryRequest request) {

        List<AssetHistoryDto> diamondHistories = diamondHistoryMapper.findDiamondHistories(request);
        long totalItems = diamondHistoryMapper.countDiamondHistories(request);

        int page = request.getPage();
        int size = request.getSize();

         return AssetHistoryListResponse.builder()
                 .histories(diamondHistories)
                 .pagination(Pagination.builder()
                         .currentPage(page)
                         .totalPage((int)Math.ceil((double) totalItems / size))
                         .totalItems(totalItems)
                         .build()
                 ).build();
    }
}