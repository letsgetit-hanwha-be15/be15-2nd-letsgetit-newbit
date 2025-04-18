package com.newbit.newbitfeatureservice.purchase.query.service;

import com.newbit.newbitfeatureservice.purchase.query.dto.request.HistoryRequest;
import com.newbit.newbitfeatureservice.purchase.query.dto.response.AssetHistoryDto;
import com.newbit.newbitfeatureservice.purchase.query.dto.response.AssetHistoryListResponse;
import com.newbit.newbitfeatureservice.purchase.query.repository.AssetHistoryMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class DiamondHistoryQueryServiceTest {

    @Mock
    private AssetHistoryMapper assetHistoryMapper;

    @InjectMocks
    private DiamondHistoryQueryService diamondHistoryQueryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getDiamondHistories_ShouldReturnCorrectResponse() {
        // given
        HistoryRequest request = new HistoryRequest();
        request.setUserId(1L);
        request.setPage(1);
        request.setSize(10);

        AssetHistoryDto dto = new AssetHistoryDto(
                1L,
                "테스트 타입",
                123L,
                100,
                0,
                1000,
                LocalDateTime.now()
        );
        List<AssetHistoryDto> mockList = Collections.singletonList(dto);
        long totalCount = 1L;

        when(assetHistoryMapper.findDiamondHistories(request)).thenReturn(mockList);
        when(assetHistoryMapper.countDiamondHistories(request)).thenReturn(totalCount);

        // when
        AssetHistoryListResponse response = diamondHistoryQueryService.getDiamondHistories(request);

        // then
        assertNotNull(response);
        assertEquals(1, response.getHistories().size());
        assertEquals(dto.getServiceType(), response.getHistories().get(0).getServiceType());
        assertEquals(1, response.getPagination().getCurrentPage());
        assertEquals(1, response.getPagination().getTotalPage());
        assertEquals(1, response.getPagination().getTotalItems());

        verify(assetHistoryMapper, times(1)).findDiamondHistories(request);
        verify(assetHistoryMapper, times(1)).countDiamondHistories(request);
    }
}