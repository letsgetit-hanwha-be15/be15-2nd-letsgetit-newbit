package com.newbit.user.service;

import com.newbit.purchase.command.application.service.PointTransactionCommandService;
import com.newbit.purchase.command.domain.PointTypeConstants;
import com.newbit.user.entity.LoginHistory;
import com.newbit.user.repository.LoginHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final LoginHistoryRepository loginHistoryRepository;
    private final PointTransactionCommandService pointTransactionCommandService;

    @Transactional
    public void handleLoginSuccess(Long userId) {
        boolean isFirstLoginToday = loginHistoryRepository.countByUserIdAndToday(userId) == 0;

        if (isFirstLoginToday) {
            pointTransactionCommandService.givePointByType(userId, PointTypeConstants.FIRST_LOGIN, null); // 로그인 포인트 지급
        }

        loginHistoryRepository.save(LoginHistory.of(userId)); // 로그인 이력 저장
    }
}