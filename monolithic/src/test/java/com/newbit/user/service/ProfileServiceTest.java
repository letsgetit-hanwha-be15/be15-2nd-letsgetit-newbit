package com.newbit.user.service;

import com.newbit.common.exception.BusinessException;
import com.newbit.common.exception.ErrorCode;
import com.newbit.user.dto.response.OtherUserProfileDTO;
import com.newbit.user.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class UserQueryServiceTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserQueryService userQueryService;

    @Test
    void getOtherUserProfile_성공() {
        // given
        Long userId = 2L;
        OtherUserProfileDTO mockProfile = new OtherUserProfileDTO();
        mockProfile.setUserName("김개발");
        mockProfile.setNickname("코딩짱");
        mockProfile.setProfileImageUrl("https://image.com/test.jpg");
        mockProfile.setJobName("백엔드");

        when(userMapper.getOtherUserProfile(userId)).thenReturn(mockProfile);

        // when
        OtherUserProfileDTO result = userQueryService.getOtherUserProfile(userId);

        // then
        assertNotNull(result);
        assertEquals("김개발", result.getUserName());
        assertEquals("코딩짱", result.getNickname());
        assertEquals("https://image.com/test.jpg", result.getProfileImageUrl());
        assertEquals("백엔드", result.getJobName());
    }

    @Test
    void getOtherUserProfile_실패_정보없음() {
        // given
        Long userId = 99L;
        when(userMapper.getOtherUserProfile(userId)).thenReturn(null);

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userQueryService.getOtherUserProfile(userId);
        });

        assertEquals(ErrorCode.USER_INFO_NOT_FOUND, exception.getErrorCode());
    }
}
