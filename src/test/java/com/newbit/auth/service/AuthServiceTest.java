package com.newbit.auth.service;

import com.newbit.auth.dto.request.LoginRequestDTO;
import com.newbit.auth.dto.response.TokenResponseDTO;
import com.newbit.auth.entity.RefreshToken;
import com.newbit.auth.jwt.JwtTokenProvider;
import com.newbit.auth.repository.RefreshTokenRepository;
import com.newbit.user.entity.User;
import com.newbit.user.entity.Authority;
import com.newbit.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private RefreshTokenRepository refreshTokenRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthService authService;

    private final String email = "test@example.com";
    private final String password = "1234";
    private final String encodedPassword = "encoded1234";
    private final String accessToken = "access-token";
    private final String refreshToken = "refresh-token";
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = User.builder()
                .userId(1L)
                .email(email)
                .password(encodedPassword)
                .nickname("tester")
                .profileImageUrl("url")
                .authority(Authority.USER)
                .build();
    }

    @Test
    void login_성공() {
        LoginRequestDTO request = new LoginRequestDTO(email, password);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);
        when(jwtTokenProvider.createToken(any(), any())).thenReturn(accessToken);
        when(jwtTokenProvider.createRefreshToken(any(), any())).thenReturn(refreshToken);
        when(jwtTokenProvider.getRefreshExpiration()).thenReturn(3600000L);

        TokenResponseDTO result = authService.login(request);

        assertEquals(email, result.getEmail());
        assertEquals(accessToken, result.getAccessToken());
        assertEquals(refreshToken, result.getRefreshToken());
        verify(refreshTokenRepository).save(any());
    }

    @Test
    void login_비밀번호_불일치() {
        LoginRequestDTO request = new LoginRequestDTO(email, "wrong");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", encodedPassword)).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> authService.login(request));
    }

    @Test
    void login_유저_없음() {
        LoginRequestDTO request = new LoginRequestDTO(email, password);

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class, () -> authService.login(request));
    }

    @Test
    void refreshToken_성공() {
        RefreshToken storedToken = new RefreshToken(email, refreshToken, new Date(System.currentTimeMillis() + 10000));

        when(jwtTokenProvider.validateToken(refreshToken)).thenReturn(true);
        when(jwtTokenProvider.getUsernameFromJWT(refreshToken)).thenReturn(email);
        when(refreshTokenRepository.findById(email)).thenReturn(Optional.of(storedToken));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(jwtTokenProvider.createToken(any(), any())).thenReturn(accessToken);
        when(jwtTokenProvider.createRefreshToken(any(), any())).thenReturn("new-refresh");
        when(jwtTokenProvider.getRefreshExpiration()).thenReturn(3600000L);

        TokenResponseDTO result = authService.refreshToken(refreshToken);

        assertEquals(accessToken, result.getAccessToken());
        assertEquals("new-refresh", result.getRefreshToken());
        verify(refreshTokenRepository).save(any());
    }

    @Test
    void refreshToken_유저없음() {
        when(jwtTokenProvider.validateToken(refreshToken)).thenReturn(true);
        when(jwtTokenProvider.getUsernameFromJWT(refreshToken)).thenReturn(email);
        when(refreshTokenRepository.findById(email)).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class, () -> authService.refreshToken(refreshToken));
    }

    @Test
    void refreshToken_만료됨() {
        RefreshToken expiredToken = new RefreshToken(email, refreshToken, new Date(System.currentTimeMillis() - 1000));

        when(jwtTokenProvider.validateToken(refreshToken)).thenReturn(true);
        when(jwtTokenProvider.getUsernameFromJWT(refreshToken)).thenReturn(email);
        when(refreshTokenRepository.findById(email)).thenReturn(Optional.of(expiredToken));

        assertThrows(BadCredentialsException.class, () -> authService.refreshToken(refreshToken));
    }

    @Test
    void refreshToken_불일치() {
        RefreshToken storedToken = new RefreshToken(email, "another-token", new Date(System.currentTimeMillis() + 10000));

        when(jwtTokenProvider.validateToken(refreshToken)).thenReturn(true);
        when(jwtTokenProvider.getUsernameFromJWT(refreshToken)).thenReturn(email);
        when(refreshTokenRepository.findById(email)).thenReturn(Optional.of(storedToken));

        assertThrows(BadCredentialsException.class, () -> authService.refreshToken(refreshToken));
    }
}
