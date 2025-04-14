package com.newbit.user.service;

import com.newbit.common.exception.BusinessException;
import com.newbit.common.exception.ErrorCode;
import com.newbit.user.dto.request.UserRequestDTO;
import com.newbit.user.entity.User;
import com.newbit.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UserService userService;

    private UserRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        requestDTO = new UserRequestDTO(
                "test@example.com",
                "password123!",
                "010-1234-5678",
                "홍길동",
                "길동이",
                ""
        );
    }

    @Test
    void registerUser_성공() {
        // given
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(anyString())).thenReturn(false);
        when(userRepository.existsByNickname(anyString())).thenReturn(false);

        User user = new User();
        when(modelMapper.map(requestDTO, User.class)).thenReturn(user);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPw");

        // when
        assertDoesNotThrow(() -> userService.registerUser(requestDTO));

        // then
        verify(userRepository, times(1)).save(user);
        assertEquals("encodedPw", user.getPassword());
    }

    @Test
    void registerUser_중복_이메일() {
        when(userRepository.existsByEmail(requestDTO.getEmail())).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class, () ->
                userService.registerUser(requestDTO)
        );
        assertEquals(ErrorCode.ALREADY_REGISTERED_EMAIL, ex.getErrorCode());
    }

    @Test
    void registerUser_중복_전화번호() {
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(requestDTO.getPhoneNumber())).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class, () ->
                userService.registerUser(requestDTO)
        );
        assertEquals(ErrorCode.ALREADY_REGISTERED_PHONENUMBER, ex.getErrorCode());
    }

    @Test
    void registerUser_중복_닉네임() {
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(any())).thenReturn(false);
        when(userRepository.existsByNickname(requestDTO.getNickname())).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class, () ->
                userService.registerUser(requestDTO)
        );
        assertEquals(ErrorCode.ALREADY_REGISTERED_NICKNAME, ex.getErrorCode());
    }
}
