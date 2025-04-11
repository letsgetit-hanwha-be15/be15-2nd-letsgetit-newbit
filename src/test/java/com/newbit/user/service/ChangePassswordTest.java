package com.newbit.user.service;

import com.newbit.auth.model.CustomUser;
import com.newbit.common.exception.BusinessException;
import com.newbit.common.exception.ErrorCode;
import com.newbit.user.entity.User;
import com.newbit.user.repository.UserRepository;
import com.newbit.user.support.PasswordValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChangePassswordTest {

    @InjectMocks
    private UserInfoService userInfoService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // 가짜 인증 객체 설정
        CustomUser customUser = CustomUser.builder()
                .email("test@example.com")
                .password("encodedOldPassword")
                .build();

        when(authentication.getPrincipal()).thenReturn(customUser);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void changePassword_성공() {
        // given
        String currentPassword = "OldPassword123!";
        String newPassword = "NewPassword123!";

        User user = User.builder()
                .email("test@example.com")
                .password("encodedOldPassword")
                .build();

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(currentPassword, user.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(newPassword)).thenReturn("encodedNewPassword");

        // when
        assertDoesNotThrow(() -> userInfoService.changePassword(currentPassword, newPassword));

        // then
        verify(userRepository).findByEmail("test@example.com");
        verify(passwordEncoder).encode(newPassword);
    }

    @Test
    void changePassword_실패_현재비밀번호_불일치() {
        // given
        String currentPassword = "wrongPassword";
        String newPassword = "NewPassword123!";

        User user = User.builder()
                .email("test@example.com")
                .password("encodedOldPassword")
                .build();

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(currentPassword, user.getPassword())).thenReturn(false);

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () ->
                userInfoService.changePassword(currentPassword, newPassword));

        assertEquals(ErrorCode.INVALID_CURRENT_PASSWORD, exception.getErrorCode());
    }

    @Test
    void changePassword_실패_새비밀번호_형식불일치() {
        // given
        String currentPassword = "OldPassword123!";
        String newPassword = "short"; // 유효성 실패

        User user = User.builder()
                .email("test@example.com")
                .password("encodedOldPassword")
                .build();

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(currentPassword, user.getPassword())).thenReturn(true);

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () ->
                userInfoService.changePassword(currentPassword, newPassword));

        assertEquals(ErrorCode.INVALID_PASSWORD_FORMAT, exception.getErrorCode());
    }
}
