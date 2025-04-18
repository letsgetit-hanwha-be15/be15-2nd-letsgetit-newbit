package com.newbit.newbitfeatureservice.purchase.command.application.service;

import com.newbit.newbitfeatureservice.client.user.UserInternalFeignClient;
import com.newbit.newbitfeatureservice.common.exception.BusinessException;
import com.newbit.newbitfeatureservice.common.exception.ErrorCode;
import com.newbit.newbitfeatureservice.purchase.command.domain.PointTypeConstants;
import com.newbit.newbitfeatureservice.purchase.command.domain.aggregate.PointHistory;
import com.newbit.newbitfeatureservice.purchase.command.domain.aggregate.PointType;
import com.newbit.newbitfeatureservice.purchase.command.domain.repository.PointHistoryRepository;
import com.newbit.newbitfeatureservice.purchase.command.domain.repository.PointTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
@Service
@Slf4j
@RequiredArgsConstructor
public class PointTransactionCommandService {
    private final PointTypeRepository pointTypeRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final UserInternalFeignClient userInternalFeignClient;

    private static final Set<Integer> ALLOWED_TIP_AMOUNTS = Set.of(20, 40, 60, 80, 100);

    public void givePointByType(Long userId, String pointTypeName, Long serviceId) {
        PointType pointType = findPointType(pointTypeName);
        Integer updatedBalance = applyPoint(userId, pointType);

        savePointHistory(userId, pointType, serviceId, updatedBalance);
    }


    public void giveTipPoint(Long coffeechatId, Long menteeId, Long mentorId, Integer amount) {
        if (!ALLOWED_TIP_AMOUNTS.contains(amount)) {
            throw new BusinessException(ErrorCode.INVALID_TIP_AMOUNT);
        }

        Integer menteeBalance = userInternalFeignClient.addPoint(menteeId, amount);
        Integer mentorBalance = userInternalFeignClient.addPoint(mentorId, amount);

        savePointHistory(
                menteeId,
                findPointType(String.format(PointTypeConstants.TIPS_PROVIDED, amount)),
                coffeechatId,
                menteeBalance
        );
        savePointHistory(
                mentorId,
                findPointType(String.format(PointTypeConstants.RECEIVE_TIPS, amount)),
                coffeechatId,
                mentorBalance
        );
    }

    private PointType findPointType(String pointTypeName) {
        return pointTypeRepository.findByPointTypeName(pointTypeName)
                .orElseThrow(() -> new BusinessException(ErrorCode.POINT_TYPE_NOT_FOUND));
    }

    private Integer applyPoint(Long userId, PointType pointType) {
        if (pointType.getIncreaseAmount() != null) {
            return userInternalFeignClient.addPoint(userId, pointType.getIncreaseAmount());
        } else {
            return userInternalFeignClient.usePoint(userId, pointType.getDecreaseAmount());
        }
    }

    @Transactional
    protected void savePointHistory(Long userId, PointType pointType, Long serviceId, Integer balance) {
        PointHistory history = PointHistory.builder()
                .userId(userId)
                .pointType(pointType)
                .serviceId(serviceId)
                .balance(balance)
                .build();
        pointHistoryRepository.save(history);
    }
}
