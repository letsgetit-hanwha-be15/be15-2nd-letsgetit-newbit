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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PurchaseCommandService {
    private final ColumnPurchaseHistoryRepository columnPurchaseHistoryRepository;
    private final DiamondHistoryRepository diamondHistoryRepository;
    private final SaleHistoryRepository saleHistoryRepository;
    private final ColumnRepository columnRepository;
    private final UserRepository userRepository;


    @Transactional
    public void purchaseColumn(Long userId, ColumnPurchaseRequest request) {
        Long columnId = request.getColumnId();

        // 1. 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        // 2. 칼럼 조회
        Column column = columnRepository.findById(columnId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        // 3. 중복 구매 여부 확인
        if (columnPurchaseHistoryRepository.existsByUserIdAndColumnId(userId, columnId)) {
            throw new BusinessException(ErrorCode.COLUMN_ALREADY_PURCHASED);
        }
        // 4. 무료 칼럼일 경우 예외 발생

        // 5. 다이아 충분한지 확인 및 차감 (내부적으로 다이아 부족 시 예외 발생)
        user.useDiamond(column.getPrice());

        // 6. 구매 내역 저장
        ColumnPurchaseHistory purchaseHistory = ColumnPurchaseHistory.of(userId, column);
        columnPurchaseHistoryRepository.save(purchaseHistory);

//        // 7. 다이아몬드 사용 내역 저장
        DiamondHistory diamondHistory = DiamondHistory.forColumnPurchase(user, column);
        diamondHistoryRepository.save(diamondHistory);
//
//        // 8. 판매 내역 저장 (칼럼의 저자 기준)
        SaleHistory saleHistory = SaleHistory.forColumn(column);
        saleHistoryRepository.save(saleHistory);
    }

}
