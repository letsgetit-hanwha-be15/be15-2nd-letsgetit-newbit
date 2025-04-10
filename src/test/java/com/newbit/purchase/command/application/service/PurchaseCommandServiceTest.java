package com.newbit.purchase.command.application.service;

import com.newbit.coffeechat.command.application.service.CoffeechatCommandService;
import com.newbit.coffeechat.query.dto.response.CoffeechatDetailResponse;
import com.newbit.coffeechat.query.dto.response.CoffeechatDto;
import com.newbit.coffeechat.query.service.CoffeechatQueryService;
import com.newbit.column.service.ColumnRequestService;
import com.newbit.common.exception.BusinessException;
import com.newbit.common.exception.ErrorCode;
import com.newbit.purchase.command.application.dto.CoffeeChatPurchaseRequest;
import com.newbit.purchase.command.application.dto.ColumnPurchaseRequest;
import com.newbit.purchase.command.domain.aggregate.ColumnPurchaseHistory;
import com.newbit.purchase.command.domain.aggregate.DiamondHistory;
import com.newbit.purchase.command.domain.aggregate.SaleHistory;
import com.newbit.purchase.command.domain.repository.ColumnPurchaseHistoryRepository;
import com.newbit.purchase.command.domain.repository.DiamondHistoryRepository;
import com.newbit.purchase.command.domain.repository.SaleHistoryRepository;
import com.newbit.user.dto.response.MentorDTO;
import com.newbit.user.service.MentorService;
import com.newbit.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PurchaseCommandServiceTest {

    @InjectMocks
    private PurchaseCommandService purchaseCommandService;

    @Mock private ColumnPurchaseHistoryRepository columnPurchaseHistoryRepository;
    @Mock private DiamondHistoryRepository diamondHistoryRepository;
    @Mock private SaleHistoryRepository saleHistoryRepository;
    @Mock private ColumnRequestService columnService;
    @Mock private UserService userService;
    @Mock private CoffeechatQueryService coffeechatQueryService;
    @Mock private MentorService mentorService;
    @Mock private CoffeechatCommandService coffeechatCommandService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void purchaseColumn_success() {
        // Given
        Long userId = 1L;
        Long columnId = 10L;
        int price = 100;
        int diamondBalance = 400;
        Long mentorId = 2L;

        ColumnPurchaseRequest request = new ColumnPurchaseRequest(columnId);

        when(columnService.getColumnPriceById(columnId)).thenReturn(price);
        when(columnPurchaseHistoryRepository.existsByUserIdAndColumnId(userId, columnId)).thenReturn(false);
        doNothing().when(userService).useDiamond(userId, price);
        when(userService.getDiamondBalance(userId)).thenReturn(diamondBalance);
        when(columnService.getMentorId(columnId)).thenReturn(mentorId);

        // When & Then
        assertDoesNotThrow(() -> purchaseCommandService.purchaseColumn(userId, request));

        verify(columnPurchaseHistoryRepository).save(any(ColumnPurchaseHistory.class));
        verify(diamondHistoryRepository).save(any(DiamondHistory.class));
        verify(saleHistoryRepository).save(any(SaleHistory.class));
    }

    @Test
    void purchaseColumn_whenAlreadyPurchased_thenThrow() {
        Long userId = 1L;
        Long columnId = 10L;
        ColumnPurchaseRequest request = new ColumnPurchaseRequest(columnId);

        when(columnService.getColumnPriceById(columnId)).thenReturn(100);
        when(columnPurchaseHistoryRepository.existsByUserIdAndColumnId(userId, columnId)).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                purchaseCommandService.purchaseColumn(userId, request));

        assertEquals(ErrorCode.COLUMN_ALREADY_PURCHASED, exception.getErrorCode());
    }

    @Test
    void purchaseColumn_whenColumnFree_thenThrow() {
        Long userId = 1L;
        Long columnId = 10L;
        ColumnPurchaseRequest request = new ColumnPurchaseRequest(columnId);

        when(columnService.getColumnPriceById(columnId)).thenReturn(0);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                purchaseCommandService.purchaseColumn(userId, request));

        assertEquals(ErrorCode.COLUMN_FREE_CANNOT_PURCHASE, exception.getErrorCode());
    }

    @Test
    void purchaseColumn_whenInsufficientDiamond_thenThrow() {
        Long userId = 1L;
        Long columnId = 10L;
        int price = 100;
        ColumnPurchaseRequest request = new ColumnPurchaseRequest(columnId);

        when(columnService.getColumnPriceById(columnId)).thenReturn(price);
        when(columnPurchaseHistoryRepository.existsByUserIdAndColumnId(userId, columnId)).thenReturn(false);
        doThrow(new BusinessException(ErrorCode.INSUFFICIENT_DIAMOND))
                .when(userService).useDiamond(userId, price);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                purchaseCommandService.purchaseColumn(userId, request));

        assertEquals(ErrorCode.INSUFFICIENT_DIAMOND, exception.getErrorCode());
    }


    @Test
    void purchaseCoffeeChat_success() {
            // given
            Long coffeechatId = 1L;
            Long menteeId = 2L;
            Long mentorId = 3L;
            int purchaseQuantity = 2;
            int price = 500;
            int balanceAfterPurchase = 1000;

            CoffeeChatPurchaseRequest request = new CoffeeChatPurchaseRequest();
            request.setCoffeechatId(coffeechatId);

            CoffeechatDto coffeechatDto = mock(CoffeechatDto.class);
            when(coffeechatDto.getMenteeId()).thenReturn(menteeId);
            when(coffeechatDto.getMentorId()).thenReturn(mentorId);
            when(coffeechatDto.getPurchaseQuantity()).thenReturn(purchaseQuantity);
            CoffeechatDetailResponse response = CoffeechatDetailResponse.builder()
                .coffeechat(coffeechatDto)
                .build();



            when(coffeechatQueryService.getCoffeechat(coffeechatId)).thenReturn(response);
            MentorDTO mentorDTO = new MentorDTO();
            mentorDTO.setPrice(price);
            when(mentorService.getMentorInfo(mentorId)).thenReturn(mentorDTO);

            when(userService.getDiamondBalance(menteeId)).thenReturn(balanceAfterPurchase);

            // when
            purchaseCommandService.purchaseCoffeeChat(request);

            // then
            verify(coffeechatCommandService).markAsPurchased(coffeechatId);
            verify(userService).useDiamond(menteeId, purchaseQuantity * price);
            verify(userService).getDiamondBalance(menteeId);

            ArgumentCaptor<DiamondHistory> diamondCaptor = ArgumentCaptor.forClass(DiamondHistory.class);
            verify(diamondHistoryRepository).save(diamondCaptor.capture());
            assertThat(diamondCaptor.getValue().getUserId()).isEqualTo(menteeId);

            ArgumentCaptor<SaleHistory> saleCaptor = ArgumentCaptor.forClass(SaleHistory.class);
            verify(saleHistoryRepository).save(saleCaptor.capture());
            assertThat(saleCaptor.getValue().getMentorId()).isEqualTo(mentorId);
    }
}
