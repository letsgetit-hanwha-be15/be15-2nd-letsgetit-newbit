package com.newbit.newbitfeatureservice.purchase.command.application.service;

import com.newbit.newbitfeatureservice.client.user.MentorFeignClient;
import com.newbit.newbitfeatureservice.client.user.UserFeignClient;
import com.newbit.newbitfeatureservice.client.user.UserInternalFeignClient;
import com.newbit.newbitfeatureservice.client.user.dto.MentorDTO;
import com.newbit.newbitfeatureservice.client.user.dto.UserDTO;
import com.newbit.newbitfeatureservice.coffeechat.command.application.service.CoffeechatCommandService;
import com.newbit.newbitfeatureservice.coffeechat.query.dto.response.Authority;
import com.newbit.newbitfeatureservice.coffeechat.query.dto.response.CoffeechatDetailResponse;
import com.newbit.newbitfeatureservice.coffeechat.query.dto.response.CoffeechatDto;
import com.newbit.newbitfeatureservice.coffeechat.query.service.CoffeechatQueryService;
import com.newbit.newbitfeatureservice.column.service.ColumnRequestService;
import com.newbit.newbitfeatureservice.common.dto.ApiResponse;
import com.newbit.newbitfeatureservice.common.exception.BusinessException;
import com.newbit.newbitfeatureservice.common.exception.ErrorCode;
import com.newbit.newbitfeatureservice.notification.command.application.service.NotificationCommandService;
import com.newbit.newbitfeatureservice.purchase.command.application.dto.CoffeeChatPurchaseRequest;
import com.newbit.newbitfeatureservice.purchase.command.application.dto.ColumnPurchaseRequest;
import com.newbit.newbitfeatureservice.purchase.command.application.dto.MentorAuthorityPurchaseRequest;
import com.newbit.newbitfeatureservice.purchase.command.domain.PointTypeConstants;
import com.newbit.newbitfeatureservice.purchase.command.domain.aggregate.*;
import com.newbit.newbitfeatureservice.purchase.command.domain.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PurchaseCommandServiceTest {

    @InjectMocks
    private PurchaseCommandService purchaseCommandService;
    @InjectMocks
    private DiamondCoffeechatTransactionCommandService diamondCoffeechatTransactionCommandService;


    @Mock
    private ColumnPurchaseHistoryRepository columnPurchaseHistoryRepository;
    @Mock
    private DiamondHistoryRepository diamondHistoryRepository;
    @Mock
    private SaleHistoryRepository saleHistoryRepository;
    @Mock
    private PointHistoryRepository pointHistoryRepository;
    @Mock
    private ColumnRequestService columnService;
    @Mock
    private UserInternalFeignClient userService;
    @Mock
    private UserFeignClient userFeignClient;
    @Mock
    private MentorFeignClient mentorService;
    private final Long userId = 1L;
    @Mock
    private CoffeechatQueryService coffeechatQueryService;
    @Mock
    private CoffeechatCommandService coffeechatCommandService;
    @Mock
    private PointTypeRepository pointTypeRepository;
    @Mock
    private NotificationCommandService notificationCommandService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(pointTypeRepository.findByPointTypeName(PointTypeConstants.MENTOR_AUTHORITY_PURCHASE))
                .thenReturn(Optional.of(PointType.builder()
                        .pointTypeId(1L)
                        .pointTypeName(PointTypeConstants.MENTOR_AUTHORITY_PURCHASE)
                        .increaseAmount(null)
                        .decreaseAmount(null)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build()));
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
        Long menteeId = 1L;
        Long mentorId = 3L;
        int purchaseQuantity = 2;
        int price = 500;
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
        when(mentorService.getMentorInfo(mentorId)).thenReturn(ApiResponse.success(mentorDTO));

        // when
        purchaseCommandService.purchaseCoffeeChat(userId, request);

        // then
        verify(coffeechatCommandService).markAsPurchased(coffeechatId);
        verify(userService).useDiamond(menteeId, purchaseQuantity * price);

        ArgumentCaptor<DiamondHistory> diamondCaptor = ArgumentCaptor.forClass(DiamondHistory.class);
        verify(diamondHistoryRepository).save(diamondCaptor.capture());
        assertThat(diamondCaptor.getValue().getUserId()).isEqualTo(menteeId);
    }

    @Test
    void purchaseMentorAuthority_successWithDiamond() {
        MentorAuthorityPurchaseRequest request = new MentorAuthorityPurchaseRequest(PurchaseAssetType.DIAMOND);

        UserDTO userDto = UserDTO.builder()
                .userId(userId)
                .authority(Authority.USER)
                .diamond(1000)
                .build();

        when(userFeignClient.getUserByUserId(userId))
                .thenReturn(ApiResponse.success(userDto));
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

        when(userFeignClient.getUserByUserId(userId))
                .thenReturn(ApiResponse.success(userDto));
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

        when(userFeignClient.getUserByUserId(userId))
                .thenReturn(ApiResponse.success(userDto));

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

        when(userFeignClient.getUserByUserId(userId))
                .thenReturn(ApiResponse.success(userDto));
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

        when(userFeignClient.getUserByUserId(userId))
                .thenReturn(ApiResponse.success(userDto));
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

        when(userFeignClient.getUserByUserId(userId))
                .thenReturn(ApiResponse.success(userDto));
        when(userService.usePoint(userId, 2000)).thenThrow(new BusinessException(ErrorCode.INSUFFICIENT_POINT));

        BusinessException ex = assertThrows(BusinessException.class, () ->
                purchaseCommandService.purchaseMentorAuthority(userId, request));

        assertEquals(ErrorCode.INSUFFICIENT_POINT, ex.getErrorCode());
    }

    @Test
    void refundCoffeeChat_success() {
        // given
        Long coffeechatId = 1L;
        Long menteeId = 100L;
        Long mentorId = 200L;
        int refundAmount = 5000;
        Integer updatedBalance = 10000;

        // 멘티 다이아 추가 후 새로운 잔액 리턴
        when(userService.addDiamond(menteeId, refundAmount)).thenReturn(updatedBalance);

        // 다이아 내역 엔티티 반환 설정
        DiamondHistory mockHistory = DiamondHistory.forCoffeechatRefund(menteeId, coffeechatId, refundAmount, updatedBalance);

        // diamondHistoryRepository.save() 호출 시 mockHistory 반환
        when(diamondHistoryRepository.save(any(DiamondHistory.class))).thenReturn(mockHistory);

        // when
        diamondCoffeechatTransactionCommandService.refundCoffeeChat(coffeechatId, menteeId, refundAmount);

        // then
        verify(userService).addDiamond(menteeId, refundAmount);
        verify(diamondHistoryRepository).save(argThat(history ->
                history.getUserId().equals(menteeId)
                        && history.getServiceId().equals(coffeechatId)
                        && history.getIncreaseAmount().equals(refundAmount)
                        && history.getBalance().equals(updatedBalance)
                        && history.getServiceType().name().equals("COFFEECHAT")
        ));
    }
}
