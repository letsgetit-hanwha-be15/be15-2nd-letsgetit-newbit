package com.newbit.purchase.command.application.service;

import com.newbit.common.exception.BusinessException;
import com.newbit.purchase.command.domain.aggregate.PointType;
import com.newbit.purchase.command.domain.repository.PointHistoryRepository;
import com.newbit.purchase.command.domain.repository.PointTypeRepository;
import com.newbit.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class PointRewardCommandServiceTest {

    @InjectMocks
    private PointRewardCommandService pointRewardCommandService;

    @Mock
    private UserService userService;

    @Mock
    private PointTypeRepository pointTypeRepository;

    @Mock
    private PointHistoryRepository pointHistoryRepository;

    @Test
    void applyPointType_shouldIncreasePointAndSaveHistory() {
        // given
        Long userId = 1L;
        String pointTypeName = "댓글 적립";
        Long serviceId = 100L;

        PointType pointType = PointType.builder()
                .pointTypeId(1L)
                .pointTypeName(pointTypeName)
                .increaseAmount(10)
                .build();

        Mockito.when(pointTypeRepository.findByPointTypeName(pointTypeName)).thenReturn(Optional.of(pointType));
        Mockito.when(userService.addPoint(userId, 10)).thenReturn(110);

        // when
        pointRewardCommandService.applyPointType(userId, pointTypeName, serviceId);

        // then
        Mockito.verify(userService).addPoint(userId, 10);
        Mockito.verify(pointHistoryRepository).save(Mockito.argThat(history ->
                history.getUserId().equals(userId)
                        && history.getServiceId().equals(serviceId)
                        && history.getBalance().equals(110)
                        && history.getPointType().equals(pointType)
        ));
    }

    @Test
    void applyPointType_shouldThrowExceptionIfPointTypeNotFound() {
        // given
        String pointTypeName = "없는타입";

        Mockito.when(pointTypeRepository.findByPointTypeName(pointTypeName))
                .thenReturn(Optional.empty());

        // when & then
        assertThrows(BusinessException.class,
                () -> pointRewardCommandService.applyPointType(1L, pointTypeName, null));
    }
}