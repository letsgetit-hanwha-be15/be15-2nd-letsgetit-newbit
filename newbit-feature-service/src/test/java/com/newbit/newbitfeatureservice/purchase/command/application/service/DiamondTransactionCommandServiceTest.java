package com.newbit.newbitfeatureservice.purchase.command.application.service;

import com.newbit.newbitfeatureservice.client.user.UserFeignClient;
import com.newbit.newbitfeatureservice.client.user.UserInternalFeignClient;
import com.newbit.newbitfeatureservice.purchase.command.domain.aggregate.DiamondHistory;
import com.newbit.newbitfeatureservice.purchase.command.domain.aggregate.DiamondTransactionType;
import com.newbit.newbitfeatureservice.purchase.command.domain.repository.DiamondHistoryRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class DiamondTransactionCommandServiceTest {

    private DiamondTransactionCommandService service;
    private DiamondHistoryRepository diamondHistoryRepository;
    private UserInternalFeignClient userService;

    @BeforeEach
    void setUp() {
        diamondHistoryRepository = mock(DiamondHistoryRepository.class);
        userService = mock(UserInternalFeignClient.class);
        service = new DiamondTransactionCommandService(diamondHistoryRepository, userService);
    }

    @Test
    void applyDiamondPayment_shouldSaveCorrectDiamondHistory() {
        // given
        Long userId = 1L;
        Long paymentId = 100L;
        Integer amount = 3000;
        Integer updatedBalance = 8000;

        when(userService.addDiamond(userId, amount)).thenReturn(updatedBalance);

        // when
        service.applyDiamondPayment(userId, paymentId, amount);

        // then
        ArgumentCaptor<DiamondHistory> captor = ArgumentCaptor.forClass(DiamondHistory.class);
        verify(diamondHistoryRepository).save(captor.capture());

        DiamondHistory saved = captor.getValue();
        assertThat(saved.getUserId()).isEqualTo(userId);
        assertThat(saved.getServiceId()).isEqualTo(paymentId);
        assertThat(saved.getServiceType()).isEqualTo(DiamondTransactionType.CHARGE);
        assertThat(saved.getIncreaseAmount()).isEqualTo(amount);
        assertThat(saved.getDecreaseAmount()).isNull();
        assertThat(saved.getBalance()).isEqualTo(updatedBalance);
    }

    @Test
    void applyDiamondRefund_shouldSaveCorrectDiamondHistory() {
        // given
        Long userId = 2L;
        Long refundId = 200L;
        Integer amount = 1500;
        Integer updatedBalance = 4000;

        when(userService.useDiamond(userId, amount)).thenReturn(updatedBalance);

        // when
        service.applyDiamondRefund(userId, refundId, amount);

        // then
        ArgumentCaptor<DiamondHistory> captor = ArgumentCaptor.forClass(DiamondHistory.class);
        verify(diamondHistoryRepository).save(captor.capture());

        DiamondHistory saved = captor.getValue();
        assertThat(saved.getUserId()).isEqualTo(userId);
        assertThat(saved.getServiceId()).isEqualTo(refundId);
        assertThat(saved.getServiceType()).isEqualTo(DiamondTransactionType.REFUND);
        assertThat(saved.getIncreaseAmount()).isNull();
        assertThat(saved.getDecreaseAmount()).isEqualTo(amount);
        assertThat(saved.getBalance()).isEqualTo(updatedBalance);
    }
}