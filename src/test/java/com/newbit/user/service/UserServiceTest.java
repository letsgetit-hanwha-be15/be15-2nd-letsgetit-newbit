package com.newbit.user.service;

import com.newbit.common.exception.BusinessException;
import com.newbit.common.exception.ErrorCode;
import com.newbit.user.dto.request.UserRequestDTO;
import com.newbit.user.entity.User;
import com.newbit.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerUser_성공() {
        // given
        UserRequestDTO dto = new UserRequestDTO("test@example.com", "1234", "010-1234-5678", "홍길동", "길동이", null);
        User user = new User();
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(modelMapper.map(dto, User.class)).thenReturn(user);
        when(passwordEncoder.encode("1234")).thenReturn("암호화된1234");

        // when
        userService.registerUser(dto);

        // then
        verify(userRepository, times(1)).save(user);
        assertEquals("암호화된1234", user.getPassword());
    }

    @Test
    void registerUser_중복이메일_예외발생() {
        // given
        UserRequestDTO dto = new UserRequestDTO("test@example.com", "1234", "010-1234-5678", "홍길동", "길동이", null);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.registerUser(dto);
        });

        assertEquals(ErrorCode.ALREADY_REGISTERED_EMAIL, exception.getErrorCode());
    }
}
