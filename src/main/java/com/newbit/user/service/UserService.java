package com.newbit.user.service;

import com.newbit.common.exception.BusinessException;
import com.newbit.common.exception.ErrorCode;
import com.newbit.user.entity.User;
import com.newbit.user.dto.request.UserRequestDTO;
import com.newbit.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    // 회원 가입
    @Transactional
    public void registerUser(UserRequestDTO request) {
        // 중복 회원 체크 로직 등 추가 가능
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.ALREADY_REGISTERED_EMAIL);
        }
        // 회원 가입
        User user = modelMapper.map(request, User.class);
        user.setEncodedPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
    }
}
