package com.newbit.newbitfeatureservice.purchase.command.application.service;

import com.newbit.newbitfeatureservice.client.user.UserInternalFeignClient;
import com.newbit.newbitfeatureservice.common.exception.BusinessException;
import com.newbit.newbitfeatureservice.common.exception.ErrorCode;
import com.newbit.newbitfeatureservice.purchase.command.domain.PointTypeConstants;
import com.newbit.newbitfeatureservice.purchase.command.domain.aggregate.PointHistory;
import com.newbit.newbitfeatureservice.purchase.command.domain.aggregate.PointType;
import com.newbit.newbitfeatureservice.purchase.command.domain.repository.PointHistoryRepository;
import com.newbit.newbitfeatureservice.purchase.command.domain.repository.PointTypeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PointTransactionCommandServiceTest {

    @InjectMocks
    private PointTransactionCommandService pointTransactionCommandService;

    @Mock
    private UserInternalFeignClient userService;

    @Mock
    private PointTypeRepository pointTypeRepository;

    @Mock
    private PointHistoryRepository pointHistoryRepository;

    @Test
    void applyPointType_shouldIncreasePointAndSaveHistory() {
        // given
        Long userId = 1L;
        String pointTypeName = PointTypeConstants.COMMENTS;
        Long serviceId = 100L;

        PointType pointType = PointType.builder()
                .pointTypeId(1L)
                .pointTypeName(pointTypeName)
                .increaseAmount(10)
                .build();

        when(pointTypeRepository.findByPointTypeName(pointTypeName)).thenReturn(Optional.of(pointType));
        when(userService.addPoint(userId, 10)).thenReturn(110);

        // when
        pointTransactionCommandService.givePointByType(userId, pointTypeName, serviceId);

        // then
        verify(userService).addPoint(userId, 10);
        verify(pointHistoryRepository).save(Mockito.argThat(history ->
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

        when(pointTypeRepository.findByPointTypeName(pointTypeName))
                .thenReturn(Optional.empty());

        // when & then
        assertThrows(BusinessException.class,
                () -> pointTransactionCommandService.givePointByType(1L, pointTypeName, null));
    }

    @Test
    void giveTipPoint_success() {
        // given
        Long reviewId = 1L;
        Long menteeId = 10L;
        Long mentorId = 20L;
        Integer amount = 40;
        Integer menteeBalance = 140;
        Integer mentorBalance = 230;

        PointType menteeType = PointType.builder()
                .pointTypeName(String.format(PointTypeConstants.TIPS_PROVIDED, amount)).increaseAmount(40).build();
        PointType mentorType = PointType.builder()
                .pointTypeName(String.format(PointTypeConstants.RECEIVE_TIPS, amount)).increaseAmount(40).build();

        when(userService.addPoint(menteeId, amount)).thenReturn(menteeBalance);
        when(userService.addPoint(mentorId, amount)).thenReturn(mentorBalance);
        when(pointTypeRepository.findByPointTypeName(String.format(PointTypeConstants.TIPS_PROVIDED, amount))).thenReturn(Optional.of(menteeType));
        when(pointTypeRepository.findByPointTypeName(String.format(PointTypeConstants.RECEIVE_TIPS, amount))).thenReturn(Optional.of(mentorType));

        // when
        pointTransactionCommandService.giveTipPoint(reviewId, menteeId, mentorId, amount);

        // then
        ArgumentCaptor<PointHistory> captor = ArgumentCaptor.forClass(PointHistory.class);
        verify(pointHistoryRepository, times(2)).save(captor.capture());

        assertThat(captor.getAllValues()).hasSize(2);
        assertThat(captor.getAllValues()).anySatisfy(history ->
                assertThat(history.getUserId()).isEqualTo(menteeId)
                        .as("멘티 내역")
        );
        assertThat(captor.getAllValues()).anySatisfy(history ->
                assertThat(history.getUserId()).isEqualTo(mentorId)
                        .as("멘토 내역")
        );
    }

    @Test
    void giveTipPoint_invalidAmount_shouldThrowException() {
        // given
        Long reviewId = 1L;
        Long menteeId = 10L;
        Long mentorId = 20L;
        Integer invalidAmount = 70;

        // when & then
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> pointTransactionCommandService.giveTipPoint(reviewId, menteeId, mentorId, invalidAmount)
        );
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_TIP_AMOUNT);
    }
}