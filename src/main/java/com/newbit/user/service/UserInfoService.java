package com.newbit.user.service;

import com.newbit.auth.model.CustomUser;
import com.newbit.common.exception.BusinessException;
import com.newbit.common.exception.ErrorCode;
import com.newbit.user.dto.request.UserInfoUpdateRequestDTO;
import com.newbit.user.dto.response.UserDTO;
import com.newbit.user.entity.User;
import com.newbit.user.repository.UserRepository;
import com.newbit.user.support.PasswordValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.newbit.common.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class UserInfoService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDTO getMyInfo(String email) {
        return userRepository.findByEmail(email)
                .map(UserDTO::fromEntity)
                .orElseThrow(() -> new BusinessException(USER_INFO_NOT_FOUND));
    }

    @Transactional
    public UserDTO updateMyInfo(UserInfoUpdateRequestDTO request) {
        CustomUser customUser = (CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = customUser.getEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(USER_INFO_NOT_FOUND));

        // 닉네임 중복 검사 (본인의 닉네임이 아니라면)
        if (!user.getNickname().equals(request.getNickname())
                && userRepository.existsByNickname(request.getNickname())) {
            throw new BusinessException(ALREADY_REGISTERED_NICKNAME); // 적절한 에러코드 필요
        }

        // 전화번호 중복 검사 (본인의 번호가 아니라면)
        if (!user.getPhoneNumber().equals(request.getPhoneNumber())
                && userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new BusinessException(ALREADY_REGISTERED_PHONENUMBER); // 적절한 에러코드 필요
        }

        user.updateInfo(request.getNickname(), request.getPhoneNumber(), request.getProfileImageUrl());

        return UserDTO.fromEntity(user);
    }

    @Transactional
    public void changePassword(String currentPassword, String newPassword) {
        // 현재 로그인한 사용자 정보 가져오기
        CustomUser customUser = (CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = customUser.getEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(USER_INFO_NOT_FOUND));

        // 기존 비밀번호 확인
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_CURRENT_PASSWORD); // 비밀번호 틀림
        }

        // 새 비밀번호 유효성 검사
        if (!PasswordValidator.isValid(newPassword)) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD_FORMAT); // 조건 불충족
        }

        // 비밀번호 변경
        user.setEncodedPassword(passwordEncoder.encode(newPassword));
    }
}
