package com.newbit.newbitfeatureservice.purchase.command.application.service;

import com.newbit.newbitfeatureservice.column.service.ColumnRequestService;
import com.newbit.newbitfeatureservice.common.exception.BusinessException;
import com.newbit.newbitfeatureservice.common.exception.ErrorCode;
import com.newbit.newbitfeatureservice.notification.command.application.dto.request.NotificationSendRequest;
import com.newbit.newbitfeatureservice.notification.command.application.service.NotificationCommandService;
import com.newbit.newbitfeatureservice.purchase.command.domain.aggregate.*;
import com.newbit.newbitfeatureservice.purchase.command.domain.repository.*;
import com.newbit.newbitfeatureservice.coffeechat.command.application.service.CoffeechatCommandService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CompletePurchaseServiceTest {

    @InjectMocks
    private CompletePurchaseService completePurchaseService;

    @Mock private ColumnPurchaseHistoryRepository columnPurchaseHistoryRepository;
    @Mock private DiamondHistoryRepository diamondHistoryRepository;
    @Mock private SaleHistoryRepository saleHistoryRepository;
    @Mock private ColumnRequestService columnRequestService;
    @Mock private NotificationCommandService notificationCommandService;
    @Mock private CoffeechatCommandService coffeechatCommandService;
    @Mock private PointHistoryRepository pointHistoryRepository;

    private final Long userId = 1L;
    private final Long columnId = 10L;
    private final Long coffeechatId = 20L;
    private final int columnPrice = 100;
    private final int balance = 500;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void completeColumnPurchase_success() {
        when(columnRequestService.getMentorId(columnId)).thenReturn(2L);

        assertDoesNotThrow(() ->
                completePurchaseService.completeColumnPurchase(userId, columnId, columnPrice, balance));

        verify(columnPurchaseHistoryRepository).save(any(ColumnPurchaseHistory.class));
        verify(diamondHistoryRepository).save(any(DiamondHistory.class));
        verify(saleHistoryRepository).save(any(SaleHistory.class));
        verify(notificationCommandService).sendNotification(any(NotificationSendRequest.class));
    }

    @Test
    void completeColumnPurchase_whenAlreadyPurchased_thenThrow() {
        doThrow(new DataIntegrityViolationException("duplicate"))
                .when(columnPurchaseHistoryRepository).save(any(ColumnPurchaseHistory.class));

        BusinessException ex = assertThrows(BusinessException.class, () ->
                completePurchaseService.completeColumnPurchase(userId, columnId, columnPrice, balance));

        assertEquals(ErrorCode.COLUMN_ALREADY_PURCHASED, ex.getErrorCode());
    }

    @Test
    void completeCoffeeChatPurchase_success() {
        Long menteeId = 3L;

        assertDoesNotThrow(() ->
                completePurchaseService.completeCoffeeChatPurchase(userId, coffeechatId, menteeId, columnPrice, balance));

        verify(coffeechatCommandService).markAsPurchased(coffeechatId);
        verify(diamondHistoryRepository).save(any(DiamondHistory.class));
        verify(notificationCommandService).sendNotification(any(NotificationSendRequest.class));
    }

    @Test
    void completeMentorAuthorityPurchase_withDiamond_success() {
        assertDoesNotThrow(() ->
                completePurchaseService.completeMentorAuthorityPurchase(userId, PurchaseAssetType.DIAMOND, balance, null));

        verify(diamondHistoryRepository).save(any(DiamondHistory.class));
        verify(notificationCommandService).sendNotification(any(NotificationSendRequest.class));
    }

    @Test
    void completeMentorAuthorityPurchase_withPoint_success() {
        PointType pointType = PointType.builder().pointTypeId(1L).pointTypeName("MENTOR_AUTHORITY_PURCHASE").build();

        assertDoesNotThrow(() ->
                completePurchaseService.completeMentorAuthorityPurchase(userId, PurchaseAssetType.POINT, balance, pointType));

        verify(pointHistoryRepository).save(any(PointHistory.class));
        verify(notificationCommandService).sendNotification(any(NotificationSendRequest.class));
    }
}
