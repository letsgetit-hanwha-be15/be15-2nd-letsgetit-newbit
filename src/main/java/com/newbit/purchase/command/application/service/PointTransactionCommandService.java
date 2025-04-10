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
public class PointTransactionCommandService {
    private final UserService userService;
    private final PointTypeRepository pointTypeRepository;
    private final PointHistoryRepository pointHistoryRepository;

    private static final Set<Integer> ALLOWED_TIP_AMOUNTS = Set.of(20, 40, 60, 80, 100);

    @Transactional
    public void givePointByType(Long userId, String pointTypeName, Long serviceId) {
        PointType pointType = findPointType(pointTypeName);
        Integer updatedBalance = applyPoint(userId, pointType);

        savePointHistory(userId, pointType, serviceId, updatedBalance);
    }

    @Transactional
    public void giveTipPoint(Long coffeechatId, Long menteeId, Long mentorId, Integer amount) {
        if (!ALLOWED_TIP_AMOUNTS.contains(amount)) {
            throw new BusinessException(ErrorCode.INVALID_TIP_AMOUNT);
        }

        Integer menteeBalance = userService.addPoint(menteeId, amount);
        Integer mentorBalance = userService.addPoint(mentorId, amount);

        savePointHistory(menteeId, findPointType("팁 " + amount + "제공"), coffeechatId, menteeBalance);
        savePointHistory(mentorId, findPointType("팁 " + amount + "수령"), coffeechatId, mentorBalance);
    }

    private PointType findPointType(String pointTypeName) {
        return pointTypeRepository.findByPointTypeName(pointTypeName)
                .orElseThrow(() -> new BusinessException(ErrorCode.POINT_TYPE_NOT_FOUND));
    }

    private Integer applyPoint(Long userId, PointType pointType) {
        if (pointType.getIncreaseAmount() != null) {
            return userService.addPoint(userId, pointType.getIncreaseAmount());
        } else {
            return userService.usePoint(userId, pointType.getDecreaseAmount());
        }
    }

    private void savePointHistory(Long userId, PointType pointType, Long serviceId, Integer balance) {
        PointHistory history = PointHistory.builder()
                .userId(userId)
                .pointType(pointType)
                .serviceId(serviceId)
                .balance(balance)
                .build();
        pointHistoryRepository.save(history);
    }
}
