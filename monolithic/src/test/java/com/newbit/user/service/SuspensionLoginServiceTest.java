package com.newbit.user.service;

import com.newbit.auth.dto.request.LoginRequestDTO;
import com.newbit.auth.dto.response.TokenResponseDTO;
import com.newbit.auth.jwt.JwtTokenProvider;
import com.newbit.auth.repository.RefreshTokenRepository;
import com.newbit.auth.service.AuthService;
import com.newbit.user.entity.Authority;
import com.newbit.user.entity.User;
import com.newbit.user.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SuspensionLoginServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private SuspensionService suspensionService;

    // Case 1: 정지된 상태이고 아직 7일이 지나지 않은 경우 → 로그인 실패
    @Test
    void loginFails_whenSuspendedAndWithin7Days() {
        // given
        User user = User.builder()
                .userId(1L)
                .email("test@example.com")
                .password("encoded")
                .isSuspended(true)
                .updatedAt(LocalDateTime.now().minusDays(2)) // 7일 안 됨
                .build();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        doNothing().when(suspensionService).checkAndSuspendUser(user.getUserId());

        // when & then
        LoginRequestDTO request = new LoginRequestDTO("test@example.com", "password");

        BadCredentialsException ex = assertThrows(BadCredentialsException.class, () -> {
            authService.login(request);
        });

        assertTrue(ex.getMessage().contains("정지 누적으로 인해 로그인할 수 없습니다"));
    }

    // Case 2: 정지 상태지만 7일이 지난 경우 → 정지 해제되고 로그인 성공
    @Test
    void loginUnblocksSuspendedUserAfter7Days() {
        // given
        User user = User.builder()
                .userId(1L)
                .email("test@example.com")
                .password("encodedPassword")
                .isSuspended(true)
                .updatedAt(LocalDateTime.now().minusDays(10)) // 7일 초과
                .authority(Authority.USER)
                .nickname("해제유저")
                .build();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        doNothing().when(suspensionService).checkAndSuspendUser(user.getUserId());
        when(passwordEncoder.matches(eq("password"), eq("encodedPassword"))).thenReturn(true);
        when(jwtTokenProvider.createToken(any(), any())).thenReturn("access-token");
        when(jwtTokenProvider.createRefreshToken(any(), any())).thenReturn("refresh-token");
        when(jwtTokenProvider.getRefreshExpiration()).thenReturn(1000000L);

        // when
        TokenResponseDTO response = authService.login(new LoginRequestDTO("test@example.com", "password"));

        // then
        assertFalse(user.getIsSuspended(), "7일이 지났기 때문에 정지 해제되어야 함");
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
    }
}
