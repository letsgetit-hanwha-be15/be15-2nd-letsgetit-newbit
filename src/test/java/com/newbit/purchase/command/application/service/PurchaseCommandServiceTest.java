package com.newbit.purchase.command.application.service;

import com.newbit.column.service.ColumnRequestService;
import com.newbit.common.exception.BusinessException;
import com.newbit.common.exception.ErrorCode;
import com.newbit.purchase.command.application.dto.ColumnPurchaseRequest;
import com.newbit.purchase.command.application.dto.MentorAuthorityPurchaseRequest;
import com.newbit.purchase.command.domain.aggregate.*;
import com.newbit.purchase.command.domain.repository.ColumnPurchaseHistoryRepository;
import com.newbit.purchase.command.domain.repository.DiamondHistoryRepository;
import com.newbit.purchase.command.domain.repository.PointHistoryRepository;
import com.newbit.purchase.command.domain.repository.SaleHistoryRepository;
import com.newbit.user.dto.response.UserDTO;
import com.newbit.user.entity.Authority;
import com.newbit.user.service.MentorService;
import com.newbit.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PurchaseCommandServiceTest {

    @InjectMocks
    private PurchaseCommandService purchaseCommandService;

    @Mock private ColumnPurchaseHistoryRepository columnPurchaseHistoryRepository;
    @Mock private DiamondHistoryRepository diamondHistoryRepository;
    @Mock private SaleHistoryRepository saleHistoryRepository;
    @Mock private PointHistoryRepository pointHistoryRepository;
    @Mock private ColumnRequestService columnService;
    @Mock private UserService userService;
    @Mock private MentorService mentorService;
    private final Long userId = 1L;

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
        when(userService.useDiamond(userId, price)).thenReturn(diamondBalance);
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
    void purchaseMentorAuthority_successWithDiamond() {
        MentorAuthorityPurchaseRequest request = new MentorAuthorityPurchaseRequest(PurchaseAssetType.DIAMOND);

        UserDTO userDto = UserDTO.builder()
                .userId(userId)
                .authority(Authority.USER)
                .diamond(1000)
                .build();

        when(userService.getUserByUserId(userId)).thenReturn(userDto);
        when(userService.useDiamond(eq(userId), anyInt())).thenReturn(300);

        assertDoesNotThrow(() -> purchaseCommandService.purchaseMentorAuthority(userId, request));

        verify(diamondHistoryRepository).save(any(DiamondHistory.class));
        verify(mentorService).createMentor(userId);
    }

    @Test
    void purchaseMentorAuthority_successWithPoint() {
        MentorAuthorityPurchaseRequest request = new MentorAuthorityPurchaseRequest(PurchaseAssetType.POINT);

        UserDTO userDto = UserDTO.builder()
                .userId(userId)
                .authority(Authority.USER)
                .point(5000)
                .build();

        when(userService.getUserByUserId(userId)).thenReturn(userDto);
        when(userService.usePoint(eq(userId), anyInt())).thenReturn(2000);

        assertDoesNotThrow(() -> purchaseCommandService.purchaseMentorAuthority(userId, request));

        verify(pointHistoryRepository).save(any(PointHistory.class));
        verify(mentorService).createMentor(userId);
    }

    @Test
    void purchaseMentorAuthority_alreadyMentor_throwsException() {
        MentorAuthorityPurchaseRequest request = new MentorAuthorityPurchaseRequest(PurchaseAssetType.DIAMOND);

        UserDTO userDto = UserDTO.builder()
                .userId(userId)
                .authority(Authority.MENTOR)
                .build();

        when(userService.getUserByUserId(userId)).thenReturn(userDto);

        BusinessException ex = assertThrows(BusinessException.class, () ->
                purchaseCommandService.purchaseMentorAuthority(userId, request));

        assertEquals(ErrorCode.ALREADY_MENTOR, ex.getErrorCode());
    }

    @Test
    void purchaseMentorAuthority_invalidAssetType_throwsException() {
        // assetType이 null 또는 정의되지 않은 경우
        MentorAuthorityPurchaseRequest request = new MentorAuthorityPurchaseRequest(null);

        UserDTO userDto = UserDTO.builder()
                .userId(userId)
                .authority(Authority.USER)
                .build();

        when(userService.getUserByUserId(userId)).thenReturn(userDto);

        BusinessException ex = assertThrows(BusinessException.class, () ->
                purchaseCommandService.purchaseMentorAuthority(userId, request));

        assertEquals(ErrorCode.INVALID_PURCHASE_TYPE, ex.getErrorCode());
    }

    @Test
    void purchaseMentorAuthority_insufficientDiamond_throwsException() {
        MentorAuthorityPurchaseRequest request = new MentorAuthorityPurchaseRequest(PurchaseAssetType.DIAMOND);

        UserDTO userDto = UserDTO.builder()
                .userId(userId)
                .authority(Authority.USER)
                .build();

        when(userService.getUserByUserId(userId)).thenReturn(userDto);
        when(userService.useDiamond(userId, 700)).thenThrow(new BusinessException(ErrorCode.INSUFFICIENT_DIAMOND));

        BusinessException ex = assertThrows(BusinessException.class, () ->
                purchaseCommandService.purchaseMentorAuthority(userId, request));

        assertEquals(ErrorCode.INSUFFICIENT_DIAMOND, ex.getErrorCode());
    }

    @Test
    void purchaseMentorAuthority_insufficientPoint_throwsException() {
        MentorAuthorityPurchaseRequest request = new MentorAuthorityPurchaseRequest(PurchaseAssetType.POINT);

        UserDTO userDto = UserDTO.builder()
                .userId(userId)
                .authority(Authority.USER)
                .build();

        when(userService.getUserByUserId(userId)).thenReturn(userDto);
        when(userService.usePoint(userId, 2000)).thenThrow(new BusinessException(ErrorCode.INSUFFICIENT_POINT));

        BusinessException ex = assertThrows(BusinessException.class, () ->
                purchaseCommandService.purchaseMentorAuthority(userId, request));

        assertEquals(ErrorCode.INSUFFICIENT_POINT, ex.getErrorCode());
    }
}