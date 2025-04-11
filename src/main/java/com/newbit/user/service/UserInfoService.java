package com.newbit.user.service;

import com.newbit.common.exception.BusinessException;
import com.newbit.user.dto.response.UserDTO;
import com.newbit.user.entity.User;
import com.newbit.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.newbit.common.exception.ErrorCode.USER_INFO_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UserInfoService {

    private final UserRepository userRepository;

    public UserDTO getMyInfo(String email) {
        return userRepository.findByEmail(email)
                .map(UserDTO::fromEntity)
                .orElseThrow(() -> new BusinessException(USER_INFO_NOT_FOUND));
    }
}
