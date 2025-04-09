package com.newbit.purchase.command.application.service;

import com.newbit.column.entity.Column;
import com.newbit.column.repository.ColumnRepository;
import com.newbit.common.exception.BusinessException;
import com.newbit.common.exception.ErrorCode;
import com.newbit.purchase.command.application.dto.ColumnPurchaseRequest;
import com.newbit.purchase.command.domain.aggregate.ColumnPurchaseHistory;
import com.newbit.purchase.command.domain.aggregate.DiamondHistory;
import com.newbit.purchase.command.domain.aggregate.SaleHistory;
import com.newbit.purchase.command.domain.repository.ColumnPurchaseHistoryRepository;
import com.newbit.purchase.command.domain.repository.DiamondHistoryRepository;
import com.newbit.purchase.command.domain.repository.SaleHistoryRepository;
import com.newbit.user.entity.User;
import com.newbit.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

class PurchaseCommandServiceTest {

    @InjectMocks
    private PurchaseCommandService purchaseCommandService;

    @Mock private ColumnPurchaseHistoryRepository columnPurchaseHistoryRepository;
    @Mock private DiamondHistoryRepository diamondHistoryRepository;
    @Mock private SaleHistoryRepository saleHistoryRepository;
    @Mock private ColumnRepository columnRepository;
    @Mock private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void purchaseColumn_success() {
        // Given
        Long userId = 1L;
        Long columnId = 2L;
        int columnPrice = 100;

        User user = mock(User.class);
        Column column = mock(Column.class);
        ColumnPurchaseRequest request = new ColumnPurchaseRequest(columnId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(columnRepository.findById(columnId)).thenReturn(Optional.of(column));
        when(columnPurchaseHistoryRepository.existsByUserUserIdAndColumnColumnId(userId, columnId)).thenReturn(false);
        when(column.getPrice()).thenReturn(columnPrice);

        // user.useDiamond() 수행 시 예외가 없어야 하므로 doNothing()이 암시됨

        // When
        assertDoesNotThrow(() -> purchaseCommandService.purchaseColumn(userId, request));

        // Then
        verify(user).useDiamond(columnPrice);
        verify(columnPurchaseHistoryRepository).save(any(ColumnPurchaseHistory.class));
        verify(diamondHistoryRepository).save(any(DiamondHistory.class));
        verify(saleHistoryRepository).save(any(SaleHistory.class));
    }

    @Test
    void purchaseColumn_userNotFound() {
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        BusinessException e = assertThrows(BusinessException.class, () ->
                purchaseCommandService.purchaseColumn(1L, new ColumnPurchaseRequest(2L)));

        assertEquals(ErrorCode.PRODUCT_NOT_FOUND, e.getErrorCode());
    }

    @Test
    void purchaseColumn_columnNotFound() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(mock(User.class)));
        when(columnRepository.findById(any())).thenReturn(Optional.empty());

        BusinessException e = assertThrows(BusinessException.class, () ->
                purchaseCommandService.purchaseColumn(userId, new ColumnPurchaseRequest(2L)));

        assertEquals(ErrorCode.PRODUCT_NOT_FOUND, e.getErrorCode());
    }

    @Test
    void purchaseColumn_duplicatePurchase() {
        Long userId = 1L;
        Long columnId = 2L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(mock(User.class)));
        when(columnRepository.findById(columnId)).thenReturn(Optional.of(mock(Column.class)));
        when(columnPurchaseHistoryRepository.existsByUserUserIdAndColumnColumnId(userId, columnId)).thenReturn(true);

        BusinessException e = assertThrows(BusinessException.class, () ->
                purchaseCommandService.purchaseColumn(userId, new ColumnPurchaseRequest(columnId)));

        assertEquals(ErrorCode.COLUMN_ALREADY_PURCHASED, e.getErrorCode());
    }

    @Test
    void purchaseColumn_insufficientDiamond() {
        Long userId = 1L;
        Long columnId = 2L;

        User user = mock(User.class);
        Column column = mock(Column.class);
        ColumnPurchaseRequest request = new ColumnPurchaseRequest(columnId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(columnRepository.findById(columnId)).thenReturn(Optional.of(column));
        when(columnPurchaseHistoryRepository.existsByUserUserIdAndColumnColumnId(userId, columnId)).thenReturn(false);
        when(column.getPrice()).thenReturn(100);

        // 다이아 부족 시 예외 던지도록 설정
        doThrow(new BusinessException(ErrorCode.INSUFFICIENT_DIAMOND))
                .when(user).useDiamond(anyInt());

        BusinessException e = assertThrows(BusinessException.class, () ->
                purchaseCommandService.purchaseColumn(userId, request));

        assertEquals(ErrorCode.INSUFFICIENT_DIAMOND, e.getErrorCode());
    }
}