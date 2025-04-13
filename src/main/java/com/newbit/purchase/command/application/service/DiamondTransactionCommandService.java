package com.newbit.purchase.command.application.service;

import com.newbit.purchase.command.domain.aggregate.DiamondHistory;
import com.newbit.purchase.command.domain.aggregate.DiamondTransactionType;
import com.newbit.purchase.command.domain.repository.DiamondHistoryRepository;
import com.newbit.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DiamondTransactionCommandService {

    private final DiamondHistoryRepository diamondHistoryRepository;
    private final UserService userService;

    public void savePointHistory(
            Long userId,
            DiamondTransactionType type,
            Integer increaseAmount,
            Integer decreaseAmount,
            Long serviceId,
            Integer balance
    ) {
        DiamondHistory history = DiamondHistory.builder()
                .userId(userId)
                .serviceType(type)
                .serviceId(serviceId)
                .increaseAmount(increaseAmount)
                .decreaseAmount(decreaseAmount)
                .balance(balance)
                .build();
        diamondHistoryRepository.save(history);
    }



    @Transactional
    public void applyDiamondPayment(Long userId, Long paymentId, Integer amount) {
        Integer balance = userService.addDiamond(userId, amount);
        savePointHistory(userId, DiamondTransactionType.CHARGE, amount, null, paymentId, balance);
    }

}
