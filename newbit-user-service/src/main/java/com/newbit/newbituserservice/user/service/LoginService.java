package com.newbit.newbituserservice.user.service;

import com.newbit.newbituserservice.client.PointTransactionClient;
import com.newbit.newbituserservice.user.entity.LoginHistory;
import com.newbit.newbituserservice.user.repository.LoginHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final LoginHistoryRepository loginHistoryRepository;
    private final PointTransactionClient pointTransactionClient;

    public void handleLoginSuccess(Long userId) {
        boolean isFirstLoginToday = loginHistoryRepository.countByUserIdAndToday(userId) == 0;

        if (isFirstLoginToday) {
            pointTransactionClient.givePointByType(userId, "첫 로그인 적립", null); // 로그인 포인트 지급
        }

        loginHistoryRepository.save(LoginHistory.of(userId)); // 로그인 이력 저장
    }
}