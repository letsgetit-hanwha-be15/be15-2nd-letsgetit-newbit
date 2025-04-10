package com.newbit.coffeechat.command.application.service;

import com.newbit.coffeechat.command.domain.aggregate.Coffeechat;
import com.newbit.coffeechat.command.domain.repository.CoffeechatRepository;
import com.newbit.coffeechat.query.dto.response.ProgressStatus;
import com.newbit.common.exception.BusinessException;
import com.newbit.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CoffeechatCommandService {

    private final CoffeechatRepository coffeechatRepository;

    @Transactional
    public void markAsPurchased(Long coffeechatId) {
        Coffeechat coffeechat = coffeechatRepository.findById(coffeechatId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COFFEECHAT_NOT_FOUND));

        if (coffeechat.getProgressStatus() != ProgressStatus.PAYMENT_WAITING) {
            throw new BusinessException(ErrorCode.COFFEECHAT_NOT_PURCHASABLE);
        }

        coffeechat.markAsPurchased();
    }
}

