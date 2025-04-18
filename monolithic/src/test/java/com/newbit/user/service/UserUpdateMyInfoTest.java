package com.newbit.user.service;

import com.newbit.auth.model.CustomUser;
import com.newbit.common.exception.BusinessException;
import com.newbit.common.exception.ErrorCode;
import com.newbit.user.dto.request.UserInfoUpdateRequestDTO;
import com.newbit.user.dto.response.UserDTO;
import com.newbit.user.entity.User;
import com.newbit.user.repository.UserRepository;
import com.newbit.user.service.UserInfoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserUpdateMyInfoTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserInfoService userInfoService;

    @Test
    void updateMyInfo_성공() {
        // given
        String email = "test@example.com";
        User user = User.builder()
                .userId(1L)
                .email(email)
                .nickname("oldNick")
                .phoneNumber("010-1234-5678")
                .build();

        UserInfoUpdateRequestDTO request = new UserInfoUpdateRequestDTO();
        ReflectionTestUtils.setField(request, "nickname", "newNick");
        ReflectionTestUtils.setField(request, "phoneNumber", "010-9999-8888");
        ReflectionTestUtils.setField(request, "profileImageUrl", "http://image.com/me.jpg");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userRepository.existsByNickname("newNick")).thenReturn(false);
        when(userRepository.existsByPhoneNumber("010-9999-8888")).thenReturn(false);

        SecurityContextHolder.getContext().setAuthentication(mockAuthentication(email));

        // when
        UserDTO result = userInfoService.updateMyInfo(request);

        // then
        assertEquals("newNick", result.getNickname());
        assertEquals("010-9999-8888", result.getPhoneNumber());
        assertEquals("http://image.com/me.jpg", result.getProfileImageUrl());
    }

    @Test
    void updateMyInfo_실패_닉네임중복() {
        // given
        String email = "test@example.com";
        User user = User.builder()
                .email(email)
                .nickname("original")
                .phoneNumber("010-1234-5678")
                .build();

        UserInfoUpdateRequestDTO request = new UserInfoUpdateRequestDTO();
        ReflectionTestUtils.setField(request, "nickname", "dupNick");
        ReflectionTestUtils.setField(request, "phoneNumber", "010-1234-5678");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userRepository.existsByNickname("dupNick")).thenReturn(true);

        SecurityContextHolder.getContext().setAuthentication(mockAuthentication(email));

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userInfoService.updateMyInfo(request);
        });

        assertEquals(ErrorCode.ALREADY_REGISTERED_NICKNAME, exception.getErrorCode());
    }

    @Test
    void updateMyInfo_실패_전화번호중복() {
        // given
        String email = "test@example.com";
        User user = User.builder()
                .email(email)
                .nickname("original")
                .phoneNumber("010-1234-5678")
                .build();

        UserInfoUpdateRequestDTO request = new UserInfoUpdateRequestDTO();
        ReflectionTestUtils.setField(request, "nickname", "original");
        ReflectionTestUtils.setField(request, "phoneNumber", "010-0000-0000");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userRepository.existsByPhoneNumber("010-0000-0000")).thenReturn(true);

        SecurityContextHolder.getContext().setAuthentication(mockAuthentication(email));

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userInfoService.updateMyInfo(request);
        });

        assertEquals(ErrorCode.ALREADY_REGISTERED_PHONENUMBER, exception.getErrorCode());
    }

    // 헬퍼: 인증된 사용자 시뮬레이션
    private Authentication mockAuthentication(String email) {
        CustomUser customUser = CustomUser.builder()
                .userId(1L)
                .email(email)
                .build();
        return new UsernamePasswordAuthenticationToken(customUser, null, Collections.emptyList());
    }
}
