package com.newbit.purchase.command.application.service;

import com.newbit.common.exception.BusinessException;
import com.newbit.common.exception.ErrorCode;
import com.newbit.purchase.command.domain.aggregate.PointHistory;
import com.newbit.purchase.command.domain.aggregate.PointType;
import com.newbit.purchase.command.domain.repository.PointHistoryRepository;
import com.newbit.purchase.command.domain.repository.PointTypeRepository;
import com.newbit.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class PointRewardCommandService {
    private final UserService userService;
    private final PointTypeRepository pointTypeRepository;
    private final PointHistoryRepository pointHistoryRepository;


    // 포인트 지급
    @Transactional
    public void givePointByType(Long userId, String pointTypeName, Long serviceId) {
        // 1. 포인트 유형 조회
        PointType pointType = pointTypeRepository.findByPointTypeName(pointTypeName)
                .orElseThrow(() -> new BusinessException(ErrorCode.POINT_TYPE_NOT_FOUND));

        // 2. 포인트 증감 처리
        Integer updatedBalance;
        if (pointType.getIncreaseAmount() != null) {
            updatedBalance = userService.addPoint(userId, pointType.getIncreaseAmount());
        } else {
            updatedBalance = userService.usePoint(userId, pointType.getDecreaseAmount());
        }

        // 3. 포인트 내역 저장
        PointHistory history = PointHistory.builder()
                .userId(userId)
                .pointType(pointType)
                .serviceId(serviceId)
                .balance(updatedBalance)
                .build();
        pointHistoryRepository.save(history);
    }


    @Transactional
    public void giveTipPoint(Long reviewId, Long menteeId, Long mentorId, Integer amount) {
        Set<Integer> ALLOWED_TIP_AMOUNTS = Set.of(20, 40, 60, 80, 100);

        if (!ALLOWED_TIP_AMOUNTS.contains(amount)) {
            throw new BusinessException(ErrorCode.INVALID_TIP_AMOUNT);
        }

        Integer menteeBalance = userService.addPoint(menteeId, amount);
        Integer mentorBalance = userService.addPoint(mentorId, amount);

        String menteePointTypeName = "팁 " + amount + "제공";
        String mentorPointTypeName = "팁 " + amount + "수령";

        PointType menteePointType = pointTypeRepository.findByPointTypeName(menteePointTypeName)
                .orElseThrow(() -> new BusinessException(ErrorCode.POINT_TYPE_NOT_FOUND));
        PointType mentorPointType = pointTypeRepository.findByPointTypeName(mentorPointTypeName)
                .orElseThrow(() -> new BusinessException(ErrorCode.POINT_TYPE_NOT_FOUND));

        PointHistory menteeHistory = PointHistory.builder()
                .userId(menteeId)
                .pointType(menteePointType)
                .serviceId(reviewId)
                .balance(menteeBalance)
                .build();
        pointHistoryRepository.save(menteeHistory);

        PointHistory mentorHistory = PointHistory.builder()
                .userId(mentorId)
                .pointType(mentorPointType)
                .serviceId(reviewId)
                .balance(mentorBalance)
                .build();
        pointHistoryRepository.save(mentorHistory);
    }
}
