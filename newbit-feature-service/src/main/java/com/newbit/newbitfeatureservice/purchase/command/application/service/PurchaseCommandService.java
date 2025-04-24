package com.newbit.newbitfeatureservice.purchase.command.application.service;

import com.newbit.newbitfeatureservice.client.user.MentorFeignClient;
import com.newbit.newbitfeatureservice.client.user.UserFeignClient;
import com.newbit.newbitfeatureservice.client.user.UserInternalFeignClient;
import com.newbit.newbitfeatureservice.client.user.dto.MentorDTO;
import com.newbit.newbitfeatureservice.client.user.dto.UserDTO;
import com.newbit.newbitfeatureservice.coffeechat.command.application.service.CoffeechatCommandService;
import com.newbit.newbitfeatureservice.coffeechat.query.dto.response.Authority;
import com.newbit.newbitfeatureservice.coffeechat.query.dto.response.CoffeechatDto;
import com.newbit.newbitfeatureservice.coffeechat.query.service.CoffeechatQueryService;
import com.newbit.newbitfeatureservice.column.service.ColumnRequestService;
import com.newbit.newbitfeatureservice.common.exception.BusinessException;
import com.newbit.newbitfeatureservice.common.exception.ErrorCode;
import com.newbit.newbitfeatureservice.notification.command.application.dto.request.NotificationSendRequest;
import com.newbit.newbitfeatureservice.notification.command.application.service.NotificationCommandService;
import com.newbit.newbitfeatureservice.purchase.command.application.dto.CoffeeChatPurchaseRequest;
import com.newbit.newbitfeatureservice.purchase.command.application.dto.ColumnPurchaseRequest;
import com.newbit.newbitfeatureservice.purchase.command.application.dto.MentorAuthorityPurchaseRequest;
import com.newbit.newbitfeatureservice.purchase.command.domain.PointTypeConstants;
import com.newbit.newbitfeatureservice.purchase.command.domain.aggregate.*;
import com.newbit.newbitfeatureservice.purchase.command.domain.repository.*;

import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PurchaseCommandService {
    private final ColumnPurchaseHistoryRepository columnPurchaseHistoryRepository;
    private final DiamondHistoryRepository diamondHistoryRepository;
    private final SaleHistoryRepository saleHistoryRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final PointTypeRepository pointTypeRepository;
    private final ColumnRequestService columnService;
    private final UserInternalFeignClient userInternalFeignClient;
    private final CoffeechatQueryService coffeechatQueryService;
    private final MentorFeignClient mentorFeignClient;
    private final UserFeignClient userFeignClient;
    private final NotificationCommandService notificationCommandService;
    private final CompletePurchaseService completePurchaseService;
    private final CoffeechatCommandService coffeechatCommandService;


    private static final int MENTOR_AUTHORITY_DIAMOND_COST = 700;
    private static final int MENTOR_AUTHORITY_POINT_COST = 2000;


    public void purchaseColumn(Long userId, ColumnPurchaseRequest request) {
        Long columnId = request.getColumnId();

        Integer columnPrice = columnService.getColumnPriceById(columnId);

        if (columnPurchaseHistoryRepository.existsByUserIdAndColumnId(userId, columnId)) {
            throw new BusinessException(ErrorCode.COLUMN_ALREADY_PURCHASED);
        }

        if (columnPrice == 0) {
            throw new BusinessException(ErrorCode.COLUMN_FREE_CANNOT_PURCHASE);
        }

        Integer balance = userInternalFeignClient.useDiamond(userId, columnPrice);


        completePurchaseService.completeColumnPurchase(userId, columnId, columnPrice, balance);
    }


    public void purchaseCoffeeChat(Long userId, CoffeeChatPurchaseRequest request) {
        Long coffeechatId = request.getCoffeechatId();

        // 커피챗, 멘티/멘토 정보 조회
        CoffeechatDto coffeeChat = coffeechatQueryService.getCoffeechat(coffeechatId).getCoffeechat();
        Long menteeId = coffeeChat.getMenteeId();
        Long mentorId = coffeeChat.getMentorId();

        if (!Objects.equals(menteeId, userId)) {
            throw new BusinessException(ErrorCode.COFFEECHAT_PURCHASE_NOT_ALLOWED);
        }

        MentorDTO mentorInfo = mentorFeignClient.getMentorInfo(mentorId).getData();
        Integer price = mentorInfo.getPrice();
        int totalPrice = coffeeChat.getPurchaseQuantity() * price;

        Integer balance = userInternalFeignClient.useDiamond(menteeId, totalPrice);

        completePurchaseService.completeCoffeeChatPurchase(userId, coffeechatId, menteeId, totalPrice, balance);
    }


    public void purchaseMentorAuthority(Long userId, MentorAuthorityPurchaseRequest request) {
        PurchaseAssetType assetType = request.getAssetType();

        // 1. 유저 조회
        UserDTO userDto = userFeignClient.getUserByUserId(userId).getData();

        PointType mentorAuthorityType = pointTypeRepository.findByPointTypeName(PointTypeConstants.MENTOR_AUTHORITY_PURCHASE)
                .orElseThrow(() -> new BusinessException(ErrorCode.POINT_TYPE_NOT_FOUND));

        //2. 이미 멘토인지 확인
        if (userDto.getAuthority() == Authority.MENTOR) {
            throw new BusinessException(ErrorCode.ALREADY_MENTOR);
        }


        // 3. 다이아 혹은 포인트 내역 생성
        if (assetType == PurchaseAssetType.DIAMOND) {
            Integer diamondBalance = userInternalFeignClient.useDiamond(userId, MENTOR_AUTHORITY_DIAMOND_COST);
            diamondHistoryRepository.save(DiamondHistory.forMentorAuthority(userId, diamondBalance, MENTOR_AUTHORITY_DIAMOND_COST));
        } else if (assetType == PurchaseAssetType.POINT) {
            Integer pointBalance = userInternalFeignClient.usePoint(userId, MENTOR_AUTHORITY_POINT_COST);
            pointHistoryRepository.save(PointHistory.forMentorAuthority(userId, mentorAuthorityType, pointBalance, MENTOR_AUTHORITY_POINT_COST));
        } else {
            throw new BusinessException(ErrorCode.INVALID_PURCHASE_TYPE);
        }


        // 4. 멘토 등록
        mentorFeignClient.createMentor(userId);

        if(assetType == PurchaseAssetType.DIAMOND){
            notificationCommandService.sendNotification(
                    new NotificationSendRequest(
                            userId,
                            13L,
                            null,
                            "멘토 권한 구매가 완료되었습니다."
                    )
            );
        }
    }
}
