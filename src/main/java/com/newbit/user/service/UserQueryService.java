package com.newbit.user.service;

import com.newbit.common.exception.BusinessException;
import com.newbit.common.exception.ErrorCode;
import com.newbit.user.dto.response.OtherUserProfileDTO;
import com.newbit.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserQueryService {

    private final UserMapper userMapper;

    public OtherUserProfileDTO getOtherUserProfile(Long userId) {
        OtherUserProfileDTO profile = userMapper.getOtherUserProfile(userId);
        if (profile == null) {
            throw new BusinessException(ErrorCode.USER_INFO_NOT_FOUND);
        }
        return profile;
    }
}
