package com.newbit.purchase.query.service;

import com.newbit.purchase.query.dto.request.HistoryRequest;
import com.newbit.purchase.query.dto.response.SaleHistoryDto;
import com.newbit.purchase.query.dto.response.SaleHistoryListResponse;
import com.newbit.purchase.query.repository.SaleHistoryMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SaleHistoryQueryServiceTest {

    @InjectMocks
    private SaleHistoryQueryService saleHistoryQueryService;

    @Mock
    private SaleHistoryMapper saleHistoryMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getSaleHistories_success() {
        // given
        HistoryRequest request = new HistoryRequest();
        request.setUserId(1L);
        request.setPage(1);
        request.setSize(10);

        SaleHistoryDto dto = SaleHistoryDto.builder()
                .settlementHistoryId(1L)
                .isSettled(true)
                .settledAt(LocalDateTime.of(2025, 4, 10, 12, 0))
                .saleAmount(new BigDecimal("500.00"))
                .serviceType("COLUMN")
                .serviceId(20L)
                .createdAt(LocalDateTime.of(2025, 4, 10, 11, 0))
                .updatedAt(LocalDateTime.of(2025, 4, 10, 11, 30))
                .mentorId(2L)
                .build();

        when(saleHistoryMapper.findSaleHistories(request)).thenReturn(List.of(dto));
        when(saleHistoryMapper.countSaleHistories(request)).thenReturn(1L);

        // when
        SaleHistoryListResponse response = saleHistoryQueryService.getSaleHistories(request);

        // then
        assertNotNull(response);
        assertEquals(1, response.getSaleHistories().size());
        assertEquals(1, response.getPagination().getTotalItems());
        assertEquals(1, response.getPagination().getCurrentPage());
        assertEquals(1, response.getPagination().getTotalPage());

        verify(saleHistoryMapper).findSaleHistories(request);
        verify(saleHistoryMapper).countSaleHistories(request);
    }
}