package com.newbit.newbitfeatureservice.purchase.query.service;

import com.newbit.newbitfeatureservice.common.dto.Pagination;
import com.newbit.newbitfeatureservice.purchase.query.dto.request.HistoryRequest;
import com.newbit.newbitfeatureservice.purchase.query.dto.response.SaleHistoryDto;
import com.newbit.newbitfeatureservice.purchase.query.dto.response.SaleHistoryListResponse;
import com.newbit.newbitfeatureservice.purchase.query.repository.SaleHistoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SaleHistoryQueryService {

    private final SaleHistoryMapper saleHistoryMapper;

    @Transactional(readOnly = true)
    public SaleHistoryListResponse getSaleHistories(HistoryRequest request) {

        List<SaleHistoryDto> saleHistories = saleHistoryMapper.findSaleHistories(request);
        long totalItems = saleHistoryMapper.countSaleHistories(request);

        int page = request.getPage();
        int size = request.getSize();

        return SaleHistoryListResponse.builder()
                .saleHistories(saleHistories)
                .pagination(Pagination.builder()
                        .currentPage(page)
                        .totalPage((int)Math.ceil((double) totalItems / size))
                        .totalItems(totalItems)
                        .build()
                ).build();
    }
}