package com.newbit.auth.service;

import com.newbit.auth.jwt.JwtTokenProvider;
import com.newbit.auth.repository.RefreshTokenRepository;
import com.newbit.user.repository.UserRepository;
import com.newbit.user.service.SuspensionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthLogOutTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SuspensionService suspensionService;

    @Mock
    private PasswordEncoder passwordEncoder; // login(), refreshToken()에서 필요하므로 포함

    @Test
    void 로그아웃_성공_테스트() {
        // given
        String refreshToken = "validRefreshToken";
        String email = "user@example.com";

        // mock 설정
        when(jwtTokenProvider.getUsernameFromJWT(refreshToken)).thenReturn(email);

        // when
        authService.logout(refreshToken);

        // then
        verify(jwtTokenProvider).validateToken(refreshToken);
        verify(jwtTokenProvider).getUsernameFromJWT(refreshToken);
        verify(refreshTokenRepository).deleteById(email);
    }
}
