package com.newbit.user.service;

import com.newbit.common.exception.BusinessException;
import com.newbit.common.exception.ErrorCode;
import com.newbit.user.dto.request.FindIdDTO;
import com.newbit.user.dto.response.UserDTO;
import com.newbit.user.dto.response.UserIdDTO;
import com.newbit.user.entity.User;
import com.newbit.user.dto.request.UserRequestDTO;
import com.newbit.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static com.newbit.common.exception.ErrorCode.FIND_EMAIL_BY_NAME_AND_PHONE_ERROR;
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

    public UserIdDTO findEmailByNameAndPhone(FindIdDTO findIdDTO) {
        return userRepository.findByUserNameAndPhoneNumber(findIdDTO.getUserName(), findIdDTO.getPhoneNumber())
                .map(UserIdDTO::from)
                .orElseThrow(() -> new BusinessException(FIND_EMAIL_BY_NAME_AND_PHONE_ERROR));
    }

    @Transactional(readOnly = true)
    public Integer getDiamondBalance(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return user.getDiamond();
    }

    @Transactional
    public Integer useDiamond(Long userId, int amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (user.getDiamond() < amount) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_DIAMOND);
        }

        user.useDiamond(amount);// 도메인 로직에 위임 (Entity 내부에 구현된 로직)
        return user.getDiamond();
    }

    @Transactional
    public Integer usePoint(Long userId, int amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (user.getPoint() < amount) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_POINT);
        }

        user.usePoint(amount);// 도메인 로직에 위임 (Entity 내부에 구현된 로직)
        return user.getPoint();
    }


    public UserDTO getUserByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return UserDTO.builder()
                .authority(user.getAuthority())
                .diamond(user.getDiamond())
                .point(user.getPoint())
                .build();
    }
}
