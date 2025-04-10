package com.newbit.user.service;

import com.newbit.common.exception.BusinessException;
import com.newbit.common.exception.ErrorCode;
import com.newbit.user.dto.request.FindIdDTO;
import com.newbit.user.dto.response.UserIdDTO;
import com.newbit.user.entity.User;
import com.newbit.user.support.MailServiceSupport;
import com.newbit.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FindEmailByNameAndPhoneTest {

    private UserService userService;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private MailServiceSupport mailServiceSupport;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        ModelMapper modelMapper = new ModelMapper();
        passwordEncoder = mock(PasswordEncoder.class);
        MailServiceSupport mailServiceSupport = mock(MailServiceSupport.class);
        userService = new UserService(userRepository, modelMapper, passwordEncoder, mailServiceSupport);

    }

    @Test
    void findEmailByNameAndPhone_success() {
        // given
        String userName = "홍길동";
        String phoneNumber = "010-1234-5678";
        String email = "hong@test.com";

        FindIdDTO findIdDTO = new FindIdDTO(userName, phoneNumber);
        User user = new User();
        // 리플렉션 방식으로 private 필드에 값 설정 (setter가 없으므로)
        try {
            java.lang.reflect.Field emailField = User.class.getDeclaredField("email");
            emailField.setAccessible(true);
            emailField.set(user, email);
        } catch (Exception e) {
            fail("User 객체에 email 필드를 설정할 수 없습니다.");
        }

        when(userRepository.findByUserNameAndPhoneNumber(userName, phoneNumber))
                .thenReturn(Optional.of(user));

        // when
        UserIdDTO result = userService.findEmailByNameAndPhone(findIdDTO);

        // then
        assertNotNull(result);
        assertEquals(email, result.getEmail());
    }

    @Test
    void findEmailByNameAndPhone_fail_throwsException() {
        // given
        String userName = "없는사람";
        String phoneNumber = "000-0000-0000";
        FindIdDTO findIdDTO = new FindIdDTO(userName, phoneNumber);

        when(userRepository.findByUserNameAndPhoneNumber(userName, phoneNumber))
                .thenReturn(Optional.empty());

        // when & then
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> userService.findEmailByNameAndPhone(findIdDTO)
        );

        assertEquals(ErrorCode.FIND_EMAIL_BY_NAME_AND_PHONE_ERROR, exception.getErrorCode());
    }
}
