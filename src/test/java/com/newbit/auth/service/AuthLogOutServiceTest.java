package com.newbit.auth.service;

import com.newbit.auth.jwt.JwtTokenProvider;
import com.newbit.auth.repository.RefreshTokenRepository;
import com.newbit.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class AuthLogOutTest {

    private AuthService authService;
    private JwtTokenProvider jwtTokenProvider;
    private RefreshTokenRepository refreshTokenRepository;
    private UserRepository userRepository; // login(), refreshToken()에 필요하지만 여기선 안 씀

    @BeforeEach
    void setUp() {
        jwtTokenProvider = mock(JwtTokenProvider.class);
        refreshTokenRepository = mock(RefreshTokenRepository.class);
        userRepository = mock(UserRepository.class); // 필요하지만 unused

        authService = new AuthService(userRepository, refreshTokenRepository, null, jwtTokenProvider);
    }

    @Test
    void 로그아웃_성공_테스트() {
        // given
        String refreshToken = "validRefreshToken";
        String email = "user@example.com";

        // mock 설정 (doNothing 필요 없음!)
        when(jwtTokenProvider.getUsernameFromJWT(refreshToken)).thenReturn(email);

        // 실행
        authService.logout(refreshToken);

        // then
        verify(jwtTokenProvider).validateToken(refreshToken); // 호출됐는지만 확인
        verify(jwtTokenProvider).getUsernameFromJWT(refreshToken);
        verify(refreshTokenRepository).deleteById(email);
    }
}
