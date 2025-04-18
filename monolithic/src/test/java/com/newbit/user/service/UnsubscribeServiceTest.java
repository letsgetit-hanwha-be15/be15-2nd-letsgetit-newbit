package com.newbit.user.service;

import com.newbit.auth.model.CustomUser;
import com.newbit.common.exception.BusinessException;
import com.newbit.common.exception.ErrorCode;
import com.newbit.user.dto.request.DeleteUserRequestDTO;
import com.newbit.user.entity.User;
import com.newbit.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UnsubscribeServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserInfoService userInfoService;

    @BeforeEach
    void setSecurityContext() {
        CustomUser customUser = CustomUser.builder()
                .userId(1L)
                .email("test@example.com")
                .build();

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(customUser);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void unsubscribeService_성공() {
        // given
        Long userId = 1L;
        String rawPassword = "Password123!";
        String encodedPassword = "encodedPassword";

        DeleteUserRequestDTO request = new DeleteUserRequestDTO();
        ReflectionTestUtils.setField(request, "password", rawPassword);

        User user = User.builder()
                .userId(userId)
                .email("test@example.com")
                .password(encodedPassword)
                .build();

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);

        // when
        userInfoService.unsubscribeService(request);

        // then
        verify(userRepository).delete(user);
    }

    @Test
    void unsubscribeService_실패_비밀번호불일치() {
        // given
        Long userId = 1L;

        DeleteUserRequestDTO request = new DeleteUserRequestDTO();
        ReflectionTestUtils.setField(request, "password", "wrongPassword");

        User user = User.builder()
                .userId(userId)
                .email("test@example.com")
                .password("encodedCorrectPassword")
                .build();

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "encodedCorrectPassword")).thenReturn(false);

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userInfoService.unsubscribeService(request);
        });

        assertEquals(ErrorCode.INVALID_CURRENT_PASSWORD, exception.getErrorCode());
    }
}
