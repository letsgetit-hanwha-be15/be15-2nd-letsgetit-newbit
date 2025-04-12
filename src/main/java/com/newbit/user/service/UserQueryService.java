package com.newbit.user.service;

import com.newbit.common.exception.BusinessException;
import com.newbit.common.exception.ErrorCode;
import com.newbit.user.dto.response.OhterUserProfileDTO;
import com.newbit.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserQueryService {

    private final UserMapper userMapper;

    public OhterUserProfileDTO getOhterUserProfile(Long userId) {
        OhterUserProfileDTO profile = userMapper.getOhterUserProfile(userId);
        if (profile == null) {
            throw new BusinessException(ErrorCode.USER_INFO_NOT_FOUND);
        }
        return profile;
    }
}
