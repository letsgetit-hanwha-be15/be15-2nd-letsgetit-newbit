package com.newbit.newbitfeatureservice.purchase.query.service;

import com.newbit.newbitfeatureservice.common.dto.Pagination;
import com.newbit.newbitfeatureservice.purchase.query.dto.request.HistoryRequest;
import com.newbit.newbitfeatureservice.purchase.query.dto.response.ColumnPurchaseHistoryDto;
import com.newbit.newbitfeatureservice.purchase.query.dto.response.ColumnPurchaseHistoryListResponse;
import com.newbit.newbitfeatureservice.purchase.query.repository.ColumnPurchaseHistoryMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ColumnPurchaseHistoryQueryServiceTest {

    @Mock
    private ColumnPurchaseHistoryMapper columnPurchaseHistoryMapper;

    @InjectMocks
    private ColumnPurchaseHistoryQueryService columnPurchaseHistoryQueryService;

    public ColumnPurchaseHistoryQueryServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getColumnPurchaseHistories_ShouldReturnResponseWithPagination() {
        // given
        HistoryRequest request = new HistoryRequest();
        request.setUserId(1L);
        request.setPage(1);
        request.setSize(10);

        List<ColumnPurchaseHistoryDto> mockList = List.of(
                ColumnPurchaseHistoryDto.builder()
                        .columnId(100L)
                        .columnTitle("테스트 칼럼")
                        .price(5000)
                        .purchasedAt(LocalDateTime.parse("2025-04-06T12:00:00"))
                        .thumbnailUrl("https://example.com/img.jpg")
                        .build()
        );

        when(columnPurchaseHistoryMapper.findColumnPurchases(request)).thenReturn(mockList);
        when(columnPurchaseHistoryMapper.countColumnPurchases(request)).thenReturn(1L);

        // when
        ColumnPurchaseHistoryListResponse result = columnPurchaseHistoryQueryService.getColumnPurchaseHistories(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getColumnPurchases()).hasSize(1);
        assertThat(result.getPagination())
                .usingRecursiveComparison()
                .isEqualTo(Pagination.builder()
                        .currentPage(1)
                        .totalPage(1)
                        .totalItems(1)
                        .build()
                );

        verify(columnPurchaseHistoryMapper, times(1)).findColumnPurchases(request);
        verify(columnPurchaseHistoryMapper, times(1)).countColumnPurchases(request);
    }
}