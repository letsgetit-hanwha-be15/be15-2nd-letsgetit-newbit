package com.newbit.user.service;

import com.newbit.common.exception.BusinessException;
import com.newbit.common.exception.ErrorCode;
import com.newbit.user.dto.response.UserDTO;
import com.newbit.user.entity.Authority;
import com.newbit.user.entity.User;
import com.newbit.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserInfoServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserInfoService userInfoService;

    @Test
    void getMyInfo_성공() {
        // given
        String email = "test@example.com";
        User user = User.builder()
                .userId(1L)
                .email(email)
                .userName("홍길동")
                .nickname("길동이")
                .phoneNumber("010-1234-5678")
                .point(100)
                .diamond(10)
                .authority(Authority.USER)
                .profileImageUrl("https://example.com/profile.jpg")
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // when
        UserDTO result = userInfoService.getMyInfo(email);

        // then
        assertNotNull(result);
        assertEquals(email, result.getEmail());
        assertEquals("홍길동", result.getUserName());
        assertEquals("길동이", result.getNickname());
        assertEquals(100, result.getPoint());
        assertEquals(10, result.getDiamond());
        assertEquals(Authority.USER, result.getAuthority());
        assertEquals("https://example.com/profile.jpg", result.getProfileImageUrl());
    }

    @Test
    void getMyInfo_실패_유저없음() {
        // given
        String email = "notfound@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userInfoService.getMyInfo(email);
        });

        // enum 값 자체를 비교 (가장 안정적)
        assertEquals(ErrorCode.USER_INFO_NOT_FOUND, exception.getErrorCode());
    }

}
