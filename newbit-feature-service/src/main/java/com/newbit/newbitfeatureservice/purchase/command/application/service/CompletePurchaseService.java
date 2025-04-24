package com.newbit.newbitfeatureservice.purchase.command.application.service;

import com.newbit.newbitfeatureservice.column.service.ColumnRequestService;
import com.newbit.newbitfeatureservice.column.service.ColumnService;
import com.newbit.newbitfeatureservice.common.exception.BusinessException;
import com.newbit.newbitfeatureservice.common.exception.ErrorCode;
import com.newbit.newbitfeatureservice.notification.command.application.dto.request.NotificationSendRequest;
import com.newbit.newbitfeatureservice.notification.command.application.service.NotificationCommandService;
import com.newbit.newbitfeatureservice.purchase.command.domain.aggregate.ColumnPurchaseHistory;
import com.newbit.newbitfeatureservice.purchase.command.domain.aggregate.DiamondHistory;
import com.newbit.newbitfeatureservice.purchase.command.domain.aggregate.SaleHistory;
import com.newbit.newbitfeatureservice.purchase.command.domain.repository.ColumnPurchaseHistoryRepository;
import com.newbit.newbitfeatureservice.purchase.command.domain.repository.DiamondHistoryRepository;
import com.newbit.newbitfeatureservice.purchase.command.domain.repository.SaleHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CompletePurchaseService {
    private final ColumnPurchaseHistoryRepository columnPurchaseHistoryRepository;
    private final DiamondHistoryRepository diamondHistoryRepository;
    private final SaleHistoryRepository saleHistoryRepository;
    private final ColumnRequestService columnRequestService;
    private final NotificationCommandService notificationCommandService;

    @Transactional
    public void completeColumnPurchase(Long userId, Long columnId, Integer columnPrice, Integer balance) {

        try {
            columnPurchaseHistoryRepository.save(ColumnPurchaseHistory.of(userId, columnId, columnPrice));
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException(ErrorCode.COLUMN_ALREADY_PURCHASED);
        }

        diamondHistoryRepository.save(DiamondHistory.forColumnPurchase(userId, columnId, columnPrice, balance));

        Long mentorId = columnRequestService.getMentorId(columnId);
        saleHistoryRepository.save(SaleHistory.forColumn(columnId, columnPrice, mentorId));

        notificationCommandService.sendNotification(
                new NotificationSendRequest(userId, 13L, columnId, "칼럼 구매가 완료되었습니다.")
        );
    }
}
