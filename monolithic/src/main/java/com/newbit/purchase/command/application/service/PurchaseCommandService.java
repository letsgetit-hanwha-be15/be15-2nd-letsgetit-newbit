package com.newbit.purchase.command.application.service;

import com.newbit.coffeechat.command.application.service.CoffeechatCommandService;
import com.newbit.coffeechat.query.dto.response.CoffeechatDto;
import com.newbit.coffeechat.query.service.CoffeechatQueryService;
import com.newbit.column.service.ColumnRequestService;
import com.newbit.common.exception.BusinessException;
import com.newbit.common.exception.ErrorCode;
import com.newbit.notification.command.application.dto.request.NotificationSendRequest;
import com.newbit.notification.command.application.service.NotificationCommandService;
import com.newbit.purchase.command.application.dto.CoffeeChatPurchaseRequest;
import com.newbit.purchase.command.application.dto.ColumnPurchaseRequest;
import com.newbit.purchase.command.application.dto.MentorAuthorityPurchaseRequest;
import com.newbit.purchase.command.domain.PointTypeConstants;
import com.newbit.purchase.command.domain.aggregate.*;
import com.newbit.purchase.command.domain.repository.*;
import com.newbit.user.dto.response.MentorDTO;
import com.newbit.user.dto.response.UserDTO;
import com.newbit.user.entity.Authority;
import com.newbit.user.service.MentorService;
import com.newbit.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
    private final UserService userService;
    private final CoffeechatQueryService coffeechatQueryService;
    private final MentorService mentorService;
    private final NotificationCommandService notificationCommandService;



    private static final int MENTOR_AUTHORITY_DIAMOND_COST = 700;
    private static final int MENTOR_AUTHORITY_POINT_COST = 2000;

    private final CoffeechatCommandService coffeechatCommandService;


    @Transactional
    public void purchaseColumn(Long userId, ColumnPurchaseRequest request) {
        Long columnId = request.getColumnId();


        // 1. 칼럼 가격 조회
        Integer columnPrice = columnService.getColumnPriceById(columnId);

        // 2. 중복 구매 여부 확인
        if (columnPurchaseHistoryRepository.existsByUserIdAndColumnId(userId, columnId)) {
            throw new BusinessException(ErrorCode.COLUMN_ALREADY_PURCHASED);
        }

        // 3. 무료 칼럼일 경우 예외 발생
        if (columnPrice == 0) {
            throw new BusinessException(ErrorCode.COLUMN_FREE_CANNOT_PURCHASE);
        }

        // 4. 다이아 충분한지 확인 및 차감 (내부에서 다이아 부족 시 예외 발생)
        Integer diamondBalance = userService.useDiamond(userId, columnPrice);

        // 5. 구매 내역 저장
        ColumnPurchaseHistory purchaseHistory = ColumnPurchaseHistory.of(userId, columnId, columnPrice);
        columnPurchaseHistoryRepository.save(purchaseHistory);

        // 6. 다이아몬드 사용 내역 저장
        DiamondHistory diamondHistory = DiamondHistory.forColumnPurchase(userId, columnId, columnPrice, diamondBalance);
        diamondHistoryRepository.save(diamondHistory);

        // 7. 멘토ID 조회
        Long mentorId = columnService.getMentorId(columnId);

        // 8. 판매 내역 저장
        SaleHistory saleHistory = SaleHistory.forColumn(columnId, columnPrice, mentorId);
        saleHistoryRepository.save(saleHistory);


        notificationCommandService.sendNotification(
                new NotificationSendRequest(
                        userId,
                        13L,
                        columnId,
                        "칼럼 구매가 완료되었습니다."
                )
        );
    }




    @Transactional
    public void purchaseCoffeeChat(Long userId, CoffeeChatPurchaseRequest request) {
        Long coffeechatId = request.getCoffeechatId();
        CoffeechatDto coffeeChat = coffeechatQueryService.getCoffeechat(coffeechatId).getCoffeechat();
        Long menteeId = coffeeChat.getMenteeId();
        Long mentorId = coffeeChat.getMentorId();

        MentorDTO mentorInfo = mentorService.getMentorInfo(mentorId);

        Integer price = mentorInfo.getPrice();

        int totalPrice = coffeeChat.getPurchaseQuantity() * price;

        if(!Objects.equals(menteeId, userId)){
            throw new BusinessException(ErrorCode.COFFEECHAT_PURCHASE_NOT_ALLOWED);
        }


        // 1. 커피챗 상태 변경
        coffeechatCommandService.markAsPurchased(coffeechatId);

        // 2. 멘티 다이아 차감
        Integer balance = userService.useDiamond(menteeId, totalPrice);

        // 3. 다이아 내역 저장
        diamondHistoryRepository.save(DiamondHistory.forCoffeechatPurchase(menteeId, coffeechatId, totalPrice, balance));

        notificationCommandService.sendNotification(
                new NotificationSendRequest(
                        userId,
                        13L,
                        coffeechatId,
                        "커피챗 구매가 완료되었습니다."
                )
        );
    }


    @Transactional
    public void purchaseMentorAuthority(Long userId, MentorAuthorityPurchaseRequest request) {
        PurchaseAssetType assetType = request.getAssetType();

        // 1. 유저 조회
        UserDTO userDto = userService.getUserByUserId(userId);

        PointType mentorAuthorityType = pointTypeRepository.findByPointTypeName(PointTypeConstants.MENTOR_AUTHORITY_PURCHASE)
                .orElseThrow(() -> new BusinessException(ErrorCode.POINT_TYPE_NOT_FOUND));

        //2. 이미 멘토인지 확인
        if (userDto.getAuthority() == Authority.MENTOR) {
            throw new BusinessException(ErrorCode.ALREADY_MENTOR);
        }


        // 3. 다이아 혹은 포인트 내역 생성
        if (assetType == PurchaseAssetType.DIAMOND) {
            Integer diamondBalance = userService.useDiamond(userId, MENTOR_AUTHORITY_DIAMOND_COST);
            diamondHistoryRepository.save(DiamondHistory.forMentorAuthority(userId, diamondBalance, MENTOR_AUTHORITY_DIAMOND_COST));
        } else if (assetType == PurchaseAssetType.POINT) {
            Integer pointBalance = userService.usePoint(userId, MENTOR_AUTHORITY_POINT_COST);
            pointHistoryRepository.save(PointHistory.forMentorAuthority(userId, mentorAuthorityType, pointBalance, MENTOR_AUTHORITY_POINT_COST));
        } else {
            throw new BusinessException(ErrorCode.INVALID_PURCHASE_TYPE);
        }


        // 4. 멘토 등록
        mentorService.createMentor(userId);

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
