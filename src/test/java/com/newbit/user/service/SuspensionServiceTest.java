package com.newbit.user.service;

import com.newbit.user.entity.User;
import com.newbit.user.port.ReportCountPort;
import com.newbit.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SuspensionServiceTest {

    @InjectMocks
    private SuspensionService suspensionService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReportCountPort reportCountPort;

    @Test
    void shouldSuspendUser_whenTotalReportCountIsMultipleOf50() {
        // given
        Long userId = 1L;

        User user = User.builder()
                .userId(userId)
                .isSuspended(false)
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();

        when(reportCountPort.getTotalReportCountByUserId(userId)).thenReturn(50);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // when
        suspensionService.checkAndSuspendUser(userId);

        // then
        assertTrue(user.getIsSuspended());
        verify(userRepository).save(user);
    }

    @Test
    void shouldNotSuspendUser_whenTotalReportCountIsNotMultipleOf50() {
        // given
        Long userId = 2L;

        when(reportCountPort.getTotalReportCountByUserId(userId)).thenReturn(49);

        // when
        suspensionService.checkAndSuspendUser(userId);

        // then
        verify(userRepository, never()).save(any());
    }
}
