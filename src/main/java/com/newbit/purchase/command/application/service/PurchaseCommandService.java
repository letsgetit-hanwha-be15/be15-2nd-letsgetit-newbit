package com.newbit.purchase.command.application.service;

import com.newbit.coffeechat.query.dto.response.CoffeechatDto;
import com.newbit.coffeechat.query.dto.response.ProgressStatus;
import com.newbit.coffeechat.query.service.CoffeechatQueryService;
import com.newbit.column.repository.ColumnRepository;
import com.newbit.column.service.ColumnRequestService;
import com.newbit.common.exception.BusinessException;
import com.newbit.common.exception.ErrorCode;
import com.newbit.purchase.command.application.dto.CoffeeChatPurchaseRequest;
import com.newbit.purchase.command.application.dto.ColumnPurchaseRequest;
import com.newbit.purchase.command.application.dto.MentorAuthorityPurchaseRequest;
import com.newbit.purchase.command.domain.aggregate.ColumnPurchaseHistory;
import com.newbit.purchase.command.domain.aggregate.DiamondHistory;
import com.newbit.purchase.command.domain.aggregate.PurchaseAssetType;
import com.newbit.purchase.command.domain.aggregate.SaleHistory;
import com.newbit.purchase.command.domain.repository.ColumnPurchaseHistoryRepository;
import com.newbit.purchase.command.domain.repository.DiamondHistoryRepository;
import com.newbit.purchase.command.domain.repository.SaleHistoryRepository;
import com.newbit.user.dto.response.MentorDTO;
import com.newbit.user.dto.response.UserDTO;
import com.newbit.user.entity.Authority;
import com.newbit.user.service.MentorService;
import com.newbit.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PurchaseCommandService {
    private final ColumnPurchaseHistoryRepository columnPurchaseHistoryRepository;
    private final DiamondHistoryRepository diamondHistoryRepository;
    private final SaleHistoryRepository saleHistoryRepository;
    private final ColumnRequestService columnService;
    private final UserService userService;
    private final CoffeechatQueryService coffeechatQueryService;
    private final MentorService mentorService;
    private final ColumnRepository columnRepository;

    private static final int MENTOR_AUTHORITY_DIAMOND_COST = 700;
    private static final int MENTOR_AUTHORITY_POINT_COST = 2000;



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
        userService.useDiamond(userId, columnPrice);

        // 6. 구매 내역 저장
        ColumnPurchaseHistory purchaseHistory = ColumnPurchaseHistory.of(userId, columnId, columnPrice);
        columnPurchaseHistoryRepository.save(purchaseHistory);

        // 7. 회원의 현재 보유 다이아값 조회
        Integer diamondBalance = userService.getDiamondBalance(userId);

        // 8. 다이아몬드 사용 내역 저장
        DiamondHistory diamondHistory = DiamondHistory.forColumnPurchase(userId, columnId, columnPrice, diamondBalance);
        diamondHistoryRepository.save(diamondHistory);

        // 9. 멘토ID 조회
        Long mentorId = columnService.getMentorId(columnId);

        // 10. 판매 내역 저장
        SaleHistory saleHistory = SaleHistory.forColumn(columnId, columnPrice, mentorId);
        saleHistoryRepository.save(saleHistory);
    }




    @Transactional
    public void purchaseCoffeeChat(CoffeeChatPurchaseRequest request) {
        Long coffeechatId = request.getCoffeechatId();
        CoffeechatDto coffeeChat = coffeechatQueryService.getCoffeechat(coffeechatId).getCoffeechat();

        ProgressStatus coffeechatStatus = coffeeChat.getProgressStatus();
        Long menteeId = coffeeChat.getMenteeId();
        Long mentorId = coffeeChat.getMentorId();

        MentorDTO mentorInfo = mentorService.getMentorInfo(mentorId);

        Integer price = mentorInfo.getPrice();


        // 1. 구매 가능한 상태인지 확인
        if (coffeechatStatus.equals(ProgressStatus.COFFEECHAT_WAITING)) {
            throw new BusinessException(ErrorCode.COFFEECHAT_NOT_PURCHASABLE);
        }

        // 2. 총 구매가격 계산
        int totalPrice = coffeeChat.getPurchaseQuantity() * price;

        // 3. 멘티 다이아 차감
        userService.useDiamond(menteeId, totalPrice);

        //TODO : coffeechat entity
        // 4. 커피챗 상태 변경 + 구매일시 < 커피챗에서 만든 서비스 호출
//        coffeechatQueryService.markAsPurchased();

        Integer balance = userService.getDiamondBalance(menteeId);

        // 5. 다이아 내역 저장
        diamondHistoryRepository.save(DiamondHistory.forCoffeechatPurchase(menteeId, coffeechatId, totalPrice, balance));

        // 6. 판매 내역 저장
        saleHistoryRepository.save(SaleHistory.forCoffeechat(mentorId, totalPrice, coffeechatId));
    }


    @Transactional
    public void purchaseMentorAuthority(MentorAuthorityPurchaseRequest request) {

        Long userId = request.getUserId();
        PurchaseAssetType assetType = request.getAssetType();

        // 1. 유저 조회
        UserDTO userDto = userService.getUserByUserId(userId);


        //2. 이미 멘토인지 확인
        if (userDto.getAuthority() == Authority.MENTOR) {
            throw new BusinessException(ErrorCode.ALREADY_MENTOR);
        }


        // 5. 재화 차감 및 히스토리 기록
        if (assetType == PurchaseAssetType.DIAMOND) {
            userService.useDiamond(userId, MENTOR_AUTHORITY_DIAMOND_COST);
            diamondHistoryRepository.save(DiamondHistory.forMentorAuthority(userId, userDto.getDiamond(), MENTOR_AUTHORITY_DIAMOND_COST));
        } else if (assetType == PurchaseAssetType.POINT) {
            userService.usePoint(userId, MENTOR_AUTHORITY_POINT_COST);
            pointHistoryRepository.save(PointHistory.forMentorAuthority(userId));
        } else {
            throw new BusinessException(ErrorCode.INVALID_PURCHASE_TYPE);
        }


        // 3. 멘토 생성 및 저장 (기본 설정 적용)
        mentorService.createMentor();
//        Mentor mentor = Mentor.createDefault(user); // 정적 메서드에서 price 등 설정
//        mentorRepository.save(mentor);

        // 4. 권한 변경
        user.grantMentorAuthority(); // User 내부 로직에서 Role 변경

    }

}
