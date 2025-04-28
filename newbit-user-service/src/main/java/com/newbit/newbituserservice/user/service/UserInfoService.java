package com.newbit.newbituserservice.user.service;


import com.newbit.newbituserservice.security.model.CustomUser;
import com.newbit.newbituserservice.common.exception.BusinessException;
import com.newbit.newbituserservice.common.exception.ErrorCode;
import com.newbit.newbituserservice.user.dto.request.DeleteUserRequestDTO;
import com.newbit.newbituserservice.user.dto.request.UserInfoUpdateRequestDTO;
import com.newbit.newbituserservice.user.dto.request.UserProfileInfoUpdateRequestDTO;
import com.newbit.newbituserservice.user.dto.response.UserDTO;
import com.newbit.newbituserservice.user.entity.*;
import com.newbit.newbituserservice.user.repository.JobRepository;
import com.newbit.newbituserservice.user.repository.TechstackRepository;
import com.newbit.newbituserservice.user.repository.UserAndTechstackRepository;
import com.newbit.newbituserservice.user.repository.UserRepository;
import com.newbit.newbituserservice.user.support.PasswordValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserInfoService {

    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final TechstackRepository techstackRepository;
    private final UserAndTechstackRepository userAndTechstackRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDTO getMyInfo(Long userId) {
        return userRepository.findById(userId)
                .map(UserDTO::fromEntity)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));
    }
    @Transactional
    public void updateMyProfileInfo(UserProfileInfoUpdateRequestDTO request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));

        // 닉네임 중복 검사 (
        if (!user.getNickname().equals(request.getNickname())
                && userRepository.existsByNickname(request.getNickname())) {
            throw new BusinessException(ErrorCode.ALREADY_REGISTERED_NICKNAME); // 적절한 에러코드 필요
        }

        user.updateProfileNicknameInfo(request.getNickname());

        // 프로필 이미지 수정
        if (request.getProfileImageUrl() != null) {
            user.updateProfileImageUrl(request.getProfileImageUrl());
        }

        // 직종 수정
        if (request.getJobName() != null) {
            Job job = jobRepository.findByJobName(request.getJobName())
                    .orElseThrow(() -> new BusinessException(ErrorCode.JOB_NOT_FOUND));
            user.setJobId(job.getJobId());
        }

        // 기술 스택 수정
        userAndTechstackRepository.deleteAllByIdUserId(userId);

        if (request.getTechstackNames() != null && !request.getTechstackNames().isEmpty()) {
            for (String techstackName : request.getTechstackNames()) {
                Techstack techstack = techstackRepository.findByTechstackName(techstackName)
                        .orElseThrow(() -> new BusinessException(ErrorCode.TECHSTACK_NOT_FOUND));

                UserAndTechstack mapping = UserAndTechstack.builder()
                        .id(new UserAndTechstackId(userId, techstack.getTechstackId()))
                        .build();

                userAndTechstackRepository.save(mapping);
            }
        }
    }



//    @Transactional
//    public UserDTO updateMyInfo(UserInfoUpdateRequestDTO request, Long userId) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));
//
//        // 닉네임 중복 검사 (본인의 닉네임이 아니라면)
//        if (!user.getNickname().equals(request.getNickname())
//                && userRepository.existsByNickname(request.getNickname())) {
//            throw new BusinessException(ErrorCode.ALREADY_REGISTERED_NICKNAME); // 적절한 에러코드 필요
//        }
//
//        // 전화번호 중복 검사 (본인의 번호가 아니라면)
//        if (!user.getPhoneNumber().equals(request.getPhoneNumber())
//                && userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
//            throw new BusinessException(ErrorCode.ALREADY_REGISTERED_PHONENUMBER); // 적절한 에러코드 필요
//        }
//
//        user.updateInfo(request.getNickname(), request.getPhoneNumber(), request.getProfileImageUrl());
//
//        return UserDTO.fromEntity(user);
//    }
    @Transactional
    public void changePassword(String currentPassword, String newPassword) {
        // 현재 로그인한 사용자 정보 가져오기
        CustomUser customUser = (CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = customUser.getEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));

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

    @Transactional
    public void unsubscribeService(DeleteUserRequestDTO request) {
        CustomUser customUser = (CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userid = customUser.getUserId();

        User user = userRepository.findByUserId(userid)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_CURRENT_PASSWORD);
        }

        userRepository.delete(user); // 실제 삭제
    }
}
