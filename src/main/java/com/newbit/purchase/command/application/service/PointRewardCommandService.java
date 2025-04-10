package com.newbit.purchase.command.application.service;

import com.newbit.common.exception.BusinessException;
import com.newbit.purchase.command.domain.aggregate.PointHistory;
import com.newbit.purchase.command.domain.repository.PointHistoryRepository;
import com.newbit.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointRewardCommandService {
    private final UserService userService;
    private final PointTypeRepository pointTypeRepository;
    private final PointHistoryRepository pointHistoryRepository;

    @Transactional
    public void applyPointType(Long userId, String pointTypeName, Long serviceId) {
        // 1. 포인트 유형 조회
        PointType pointType = pointTypeRepository.findByPointTypeName(pointTypeName)
                .orElseThrow(() -> new BusinessException(ErrorCode.POINT_TYPE_NOT_FOUND));

        // 2. 포인트 증감 처리
        Integer updatedBalance;
        if (pointType.getIncreaseAmount() != null) {
            updatedBalance = userService.increasePoint(userId, pointType.getIncreaseAmount());
        } else {
            updatedBalance = userService.decreasePoint(userId, pointType.getDecreaseAmount());
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
}
