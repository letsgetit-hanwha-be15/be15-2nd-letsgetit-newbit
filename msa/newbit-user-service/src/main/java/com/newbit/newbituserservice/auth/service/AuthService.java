package com.newbit.newbituserservice.auth.service;


import com.newbit.newbituserservice.auth.dto.request.LoginRequestDTO;
import com.newbit.newbituserservice.auth.dto.response.TokenResponseDTO;
import com.newbit.newbituserservice.auth.entity.RefreshToken;
import com.newbit.newbituserservice.security.JwtTokenProvider;
import com.newbit.newbituserservice.auth.repository.RefreshTokenRepository;
import com.newbit.newbituserservice.user.entity.User;
import com.newbit.newbituserservice.user.repository.UserRepository;
import com.newbit.newbituserservice.user.service.SuspensionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final SuspensionService suspensionService;

    public TokenResponseDTO login(LoginRequestDTO request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("올바르지 않은 아이디 혹은 비밀번호"));

//         정지 조건 검사 추가 (신고 수 50의 배수 여부)
        suspensionService.checkAndSuspendUser(user.getUserId());

        // 정지 상태라면 차단 또는 해제
        if (user.getIsSuspended() != null && user.getIsSuspended()) {
            LocalDateTime suspendedUntil = user.getUpdatedAt().plusDays(7);

            if (LocalDateTime.now().isBefore(suspendedUntil)) {
                throw new BadCredentialsException("정지 누적으로 인해 로그인할 수 없습니다. "
                        + suspendedUntil.toLocalDate() + " 이후에 로그인해주세요.");
            } else {
                // 정지 해제
                user.setIsSuspended(false);
                user.setUpdatedAt(LocalDateTime.now());
                userRepository.save(user);
            }
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("올바르지 않은 아이디 혹은 비밀번호");
        }

        String accessToken = jwtTokenProvider.createToken(user.getEmail(), user.getAuthority().name(), user.getUserId());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail(), user.getAuthority().name(), user.getUserId());

        RefreshToken tokenEntity = RefreshToken.builder()
                .email(user.getEmail())
                .token(refreshToken)
                .expiryDate(new Date(System.currentTimeMillis() + jwtTokenProvider.getRefreshExpiration()))
                .build();

        refreshTokenRepository.save(tokenEntity);

        return TokenResponseDTO.builder()
                .userId(Long.valueOf(user.getUserId()))
                .email(user.getEmail())
                .nickname(user.getNickname())
                .authority(user.getAuthority().name())
                .profileImageUrl(user.getProfileImageUrl())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public TokenResponseDTO refreshToken(String providedRefreshToken) {
        jwtTokenProvider.validateToken(providedRefreshToken);
        String email = jwtTokenProvider.getUsernameFromJWT(providedRefreshToken);

        RefreshToken storedToken = refreshTokenRepository.findById(email)
                .orElseThrow(() -> new BadCredentialsException("해당 유저로 조회되는 리프레시 토큰 없음"));

        if (!storedToken.getToken().equals(providedRefreshToken)) {
            throw new BadCredentialsException("리프레시 토큰 일치하지 않음");
        }

        if (storedToken.getExpiryDate().before(new Date())) {
            throw new BadCredentialsException("리프레시 토큰 유효시간 만료");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("해당 리프레시 토큰을 위한 유저 없음"));

        String accessToken = jwtTokenProvider.createToken(user.getEmail(), user.getAuthority().name(), user.getUserId());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail(), user.getAuthority().name(), user.getUserId());

        RefreshToken tokenEntity = RefreshToken.builder()
                .email(user.getEmail())
                .token(refreshToken)
                .expiryDate(new Date(System.currentTimeMillis() + jwtTokenProvider.getRefreshExpiration()))
                .build();

        refreshTokenRepository.save(tokenEntity);

        return TokenResponseDTO.builder()
                .userId(Long.valueOf(user.getUserId()))
                .email(user.getEmail())
                .nickname(user.getNickname())
                .authority(user.getAuthority().name())
                .profileImageUrl(user.getProfileImageUrl())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public void logout(String refreshToken) {
        jwtTokenProvider.validateToken(refreshToken);
        String email = jwtTokenProvider.getUsernameFromJWT(refreshToken);
        refreshTokenRepository.deleteById(email);
    }
}
