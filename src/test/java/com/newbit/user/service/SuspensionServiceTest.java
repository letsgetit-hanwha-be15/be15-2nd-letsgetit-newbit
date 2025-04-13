package com.newbit.user.service;

import com.newbit.user.entity.User;
import com.newbit.user.repository.UserRepository;
import com.newbit.post.repository.PostRepository;
import com.newbit.post.repository.CommentRepository;
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
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    // 총 신고 수가 50의 배수이면 정지되어야 함
    @Test
    void shouldSuspendUser_whenTotalReportCountIsMultipleOf50() {
        // given
        Long userId = 1L;

        User user = User.builder()
                .userId(userId)
                .isSuspended(false)
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();

        when(postRepository.sumReportCountByUserId(userId)).thenReturn(20);
        when(commentRepository.sumReportCountByUserId(userId)).thenReturn(30); // 총 50
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // when
        suspensionService.checkAndSuspendUser(userId);

        // then
        assertTrue(user.getIsSuspended());
        verify(userRepository).save(user);
    }

    // 총 신고 수가 50 미만 또는 배수가 아니면 정지되지 않아야 함
    @Test
    void shouldNotSuspendUser_whenTotalReportCountIsNotMultipleOf50() {
        // given
        Long userId = 2L;

        User user = User.builder()
                .userId(userId)
                .isSuspended(false)
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();

        when(postRepository.sumReportCountByUserId(userId)).thenReturn(23);
        when(commentRepository.sumReportCountByUserId(userId)).thenReturn(26); // 총 49

        // userRepository.findById()는 호출되지 않으므로 stubbing 제거

        // when
        suspensionService.checkAndSuspendUser(userId);

        // then
        assertFalse(user.getIsSuspended());
        verify(userRepository, never()).save(any());
    }
}
