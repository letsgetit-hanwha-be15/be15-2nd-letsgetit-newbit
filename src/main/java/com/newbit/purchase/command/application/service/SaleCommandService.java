package com.newbit.purchase.command.application.service;

import com.newbit.purchase.command.domain.aggregate.SaleHistory;
import com.newbit.purchase.command.domain.repository.SaleHistoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SaleCommandService {
    SaleHistoryRepository saleHistoryRepository;

    @Transactional
    public void addSaleHistory(Long mentorId, Integer price, Long serviceId) {
        SaleHistory saleHistory = SaleHistory.forCoffeechat(mentorId, price, serviceId);
        saleHistoryRepository.save(saleHistory);
    }
}
