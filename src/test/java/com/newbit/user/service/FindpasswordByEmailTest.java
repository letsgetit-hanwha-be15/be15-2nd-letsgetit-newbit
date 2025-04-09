package com.newbit.user.service;

import com.newbit.common.exception.BusinessException;
import com.newbit.common.exception.ErrorCode;
import com.newbit.user.entity.User;
import com.newbit.user.repository.UserRepository;
import com.newbit.user.support.MailServiceSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FindPasswordByEmailTest {

    private UserService userService;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private MailServiceSupport mailServiceSupport;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        mailServiceSupport = mock(MailServiceSupport.class);
        ModelMapper modelMapper = new ModelMapper();

        userService = new UserService(userRepository, modelMapper, passwordEncoder, mailServiceSupport);
    }

    @Test
    void findPasswordByEmail_success() {
        // given
        String email = "test@example.com";
        User mockUser = mock(User.class);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        // when
        userService.findPasswordByEmail(email);

        // then
        verify(userRepository).findByEmail(email); // 유저 조회 확인
        verify(mockUser).findPassword("encodedPassword"); // 비밀번호 설정 확인
        verify(mailServiceSupport).sendMailSupport(eq(email), anyString(), contains("임시 비밀번호")); // 메일 전송 확인
    }

    @Test
    void findPasswordByEmail_userNotFound_throwsException() {
        // given
        String email = "notfound@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // when & then
        BusinessException ex = assertThrows(BusinessException.class, () -> {
            userService.findPasswordByEmail(email);
        });

        assertEquals(ErrorCode.USER_NOT_FOUND, ex.getErrorCode());
        verify(mailServiceSupport, never()).sendMailSupport(any(), any(), any());
    }
}
