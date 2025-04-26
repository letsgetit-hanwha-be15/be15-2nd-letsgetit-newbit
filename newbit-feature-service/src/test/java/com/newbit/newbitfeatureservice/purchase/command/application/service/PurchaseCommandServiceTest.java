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
import com.newbit.newbitfeatureservice.purchase.command.application.dto.*;
import com.newbit.newbitfeatureservice.purchase.command.domain.PointTypeConstants;
import com.newbit.newbitfeatureservice.purchase.command.domain.aggregate.*;
import com.newbit.newbitfeatureservice.purchase.command.domain.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PurchaseCommandServiceTest {

    @InjectMocks
    private PurchaseCommandService purchaseCommandService;

    @Mock private DiamondHistoryRepository diamondHistoryRepository;
    @Mock private PointHistoryRepository pointHistoryRepository;
    @Mock private UserInternalFeignClient userService;
    @Mock private UserFeignClient userFeignClient;
    @Mock private MentorFeignClient mentorService;
    @Mock private PointTypeRepository pointTypeRepository;
    @Mock private CompletePurchaseService completePurchaseService;

    private final Long userId = 1L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(pointTypeRepository.findByPointTypeName(PointTypeConstants.MENTOR_AUTHORITY_PURCHASE))
                .thenReturn(Optional.of(PointType.builder()
                        .pointTypeId(1L)
                        .pointTypeName(PointTypeConstants.MENTOR_AUTHORITY_PURCHASE)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build()));
    }

    @Test
    void purchaseMentorAuthority_successWithDiamond() {
        MentorAuthorityPurchaseRequest request = new MentorAuthorityPurchaseRequest(PurchaseAssetType.DIAMOND);
        UserDTO userDto = UserDTO.builder().userId(userId).authority(Authority.USER).build();

        when(userFeignClient.getUserByUserId(userId)).thenReturn(ApiResponse.success(userDto));
        when(userService.useDiamond(userId, 700)).thenReturn(300);

        assertDoesNotThrow(() -> purchaseCommandService.purchaseMentorAuthority(userId, request));

        verify(userService).useDiamond(userId, 700);
        verify(completePurchaseService)
                .completeMentorAuthorityPurchase(eq(userId), eq(PurchaseAssetType.DIAMOND), eq(300), any());
        verify(mentorService).createMentor(userId);
    }

    @Test
    void purchaseMentorAuthority_successWithPoint() {
        MentorAuthorityPurchaseRequest request = new MentorAuthorityPurchaseRequest(PurchaseAssetType.POINT);
        UserDTO userDto = UserDTO.builder().userId(userId).authority(Authority.USER).build();

        when(userFeignClient.getUserByUserId(userId)).thenReturn(ApiResponse.success(userDto));
        when(userService.usePoint(userId, 2000)).thenReturn(1000);

        assertDoesNotThrow(() -> purchaseCommandService.purchaseMentorAuthority(userId, request));

        verify(userService).usePoint(userId, 2000);
        verify(completePurchaseService)
                .completeMentorAuthorityPurchase(eq(userId), eq(PurchaseAssetType.POINT), eq(1000), any(PointType.class));
        verify(mentorService).createMentor(userId);
    }



    @Test
    void purchaseMentorAuthority_invalidAssetType_throwsException() {
        MentorAuthorityPurchaseRequest request = new MentorAuthorityPurchaseRequest(null);
        UserDTO userDto = UserDTO.builder().userId(userId).authority(Authority.USER).build();

        when(userFeignClient.getUserByUserId(userId)).thenReturn(ApiResponse.success(userDto));

        BusinessException ex = assertThrows(BusinessException.class, () ->
                purchaseCommandService.purchaseMentorAuthority(userId, request));

        assertEquals(ErrorCode.INVALID_PURCHASE_TYPE, ex.getErrorCode());
    }

    @Test
    void purchaseMentorAuthority_alreadyMentor_throwsException() {
        MentorAuthorityPurchaseRequest request = new MentorAuthorityPurchaseRequest(PurchaseAssetType.DIAMOND);
        UserDTO userDto = UserDTO.builder().userId(userId).authority(Authority.MENTOR).build();

        when(userFeignClient.getUserByUserId(userId)).thenReturn(ApiResponse.success(userDto));

        BusinessException ex = assertThrows(BusinessException.class, () ->
                purchaseCommandService.purchaseMentorAuthority(userId, request));

        assertEquals(ErrorCode.ALREADY_MENTOR, ex.getErrorCode());
    }

    @Test
    void purchaseMentorAuthority_insufficientDiamond_throwsException() {
        MentorAuthorityPurchaseRequest request = new MentorAuthorityPurchaseRequest(PurchaseAssetType.DIAMOND);
        UserDTO userDto = UserDTO.builder().userId(userId).authority(Authority.USER).build();

        when(userFeignClient.getUserByUserId(userId)).thenReturn(ApiResponse.success(userDto));
        when(userService.useDiamond(userId, 700)).thenThrow(new BusinessException(ErrorCode.INSUFFICIENT_DIAMOND));

        BusinessException ex = assertThrows(BusinessException.class, () ->
                purchaseCommandService.purchaseMentorAuthority(userId, request));

        assertEquals(ErrorCode.INSUFFICIENT_DIAMOND, ex.getErrorCode());
        verify(diamondHistoryRepository, never()).save(any());
        verify(mentorService, never()).createMentor(any());
    }

    @Test
    void purchaseMentorAuthority_insufficientPoint_throwsException() {
        MentorAuthorityPurchaseRequest request = new MentorAuthorityPurchaseRequest(PurchaseAssetType.POINT);
        UserDTO userDto = UserDTO.builder().userId(userId).authority(Authority.USER).build();

        when(userFeignClient.getUserByUserId(userId)).thenReturn(ApiResponse.success(userDto));
        when(userService.usePoint(userId, 2000)).thenThrow(new BusinessException(ErrorCode.INSUFFICIENT_POINT));

        BusinessException ex = assertThrows(BusinessException.class, () ->
                purchaseCommandService.purchaseMentorAuthority(userId, request));

        assertEquals(ErrorCode.INSUFFICIENT_POINT, ex.getErrorCode());
        verify(pointHistoryRepository, never()).save(any());
        verify(mentorService, never()).createMentor(any());
    }
}
