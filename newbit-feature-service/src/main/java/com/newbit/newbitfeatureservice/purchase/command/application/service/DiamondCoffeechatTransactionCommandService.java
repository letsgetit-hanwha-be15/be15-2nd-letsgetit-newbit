package com.newbit.newbitfeatureservice.purchase.command.application.service;

import com.newbit.newbitfeatureservice.client.user.UserInternalFeignClient;
import com.newbit.newbitfeatureservice.purchase.command.domain.aggregate.DiamondHistory;
import com.newbit.newbitfeatureservice.purchase.command.domain.aggregate.SaleHistory;
import com.newbit.newbitfeatureservice.purchase.command.domain.repository.DiamondHistoryRepository;
import com.newbit.newbitfeatureservice.purchase.command.domain.repository.SaleHistoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DiamondCoffeechatTransactionCommandService {
    private final SaleHistoryRepository saleHistoryRepository;
    private final UserInternalFeignClient userInternalFeignClient;
    private final DiamondHistoryRepository diamondHistoryRepository;

    @Transactional
    public void addSaleHistory(Long mentorId, Integer price, Long serviceId) {
        SaleHistory saleHistory = SaleHistory.forCoffeechat(mentorId, price, serviceId);
        saleHistoryRepository.save(saleHistory);
    }

    // 커피챗 다이아 환불

    public void refundCoffeeChat(Long coffeechatId, Long menteeId, Integer totalPrice) {

        // 1. 멘티 다이아 추가 후 현재 다이아값 반환
        Integer balance = userInternalFeignClient.addDiamond(menteeId, totalPrice);

        // 2. 다이아 내역 저장
        diamondHistoryRepository.save(DiamondHistory.forCoffeechatRefund(menteeId, coffeechatId, totalPrice, balance));
    }

}
