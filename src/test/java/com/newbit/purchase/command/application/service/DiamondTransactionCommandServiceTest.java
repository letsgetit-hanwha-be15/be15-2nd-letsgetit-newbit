package com.newbit.purchase.command.application.service;

import com.newbit.purchase.command.domain.aggregate.DiamondHistory;
import com.newbit.purchase.command.domain.aggregate.DiamondTransactionType;
import com.newbit.purchase.command.domain.repository.DiamondHistoryRepository;
import com.newbit.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

class DiamondTransactionCommandServiceTest {

    private DiamondTransactionCommandService diamondTransactionCommandService;
    private DiamondHistoryRepository diamondHistoryRepository;
    private UserService userService;

    @BeforeEach
    void setUp() {
        diamondHistoryRepository = mock(DiamondHistoryRepository.class);
        userService = mock(UserService.class);
        diamondTransactionCommandService = new DiamondTransactionCommandService(diamondHistoryRepository, userService);
    }

    @Test
    void applyDiamondPayment_success() {
        // given
        Long userId = 1L;
        Long paymentId = 100L;
        Integer amount = 5000;
        Integer updatedBalance = 8000;

        when(userService.addDiamond(userId, amount)).thenReturn(updatedBalance);

        // when
        diamondTransactionCommandService.applyDiamondPayment(userId, paymentId, amount);

        // then
        ArgumentCaptor<DiamondHistory> captor = ArgumentCaptor.forClass(DiamondHistory.class);
        verify(diamondHistoryRepository, times(1)).save(captor.capture());

        DiamondHistory saved = captor.getValue();
        assertThat(saved.getUserId()).isEqualTo(userId);
        assertThat(saved.getServiceType()).isEqualTo(DiamondTransactionType.CHARGE);
        assertThat(saved.getServiceId()).isEqualTo(paymentId);
        assertThat(saved.getIncreaseAmount()).isEqualTo(amount);
        assertThat(saved.getDecreaseAmount()).isNull();
        assertThat(saved.getBalance()).isEqualTo(updatedBalance);
    }
}