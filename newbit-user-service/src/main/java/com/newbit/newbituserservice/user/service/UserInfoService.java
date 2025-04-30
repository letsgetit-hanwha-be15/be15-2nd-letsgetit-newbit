package com.newbit.newbituserservice.user.service;


import com.newbit.newbituserservice.security.model.CustomUser;
import com.newbit.newbituserservice.common.exception.BusinessException;
import com.newbit.newbituserservice.common.exception.ErrorCode;
import com.newbit.newbituserservice.user.dto.request.*;
import com.newbit.newbituserservice.user.dto.response.UserDTO;
import com.newbit.newbituserservice.user.entity.*;
import com.newbit.newbituserservice.user.repository.*;
import com.newbit.newbituserservice.user.support.PasswordValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class UserInfoService {

    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final TechstackRepository techstackRepository;
    private final UserAndTechstackRepository userAndTechstackRepository;
    private final MentorRepository mentorRepository;
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



    @Transactional
    public void updateMyInfo(UserInfoUpdateRequestDTO request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));

        // 전화번호 수정
        if (!user.getPhoneNumber().equals(request.getPhoneNumber())
                && userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new BusinessException(ErrorCode.ALREADY_REGISTERED_PHONENUMBER);
        }
        if (!request.getPhoneNumber().matches("\\d+")) {
            throw new BusinessException(ErrorCode.INVALID_PHONE_NUMBER_FORMAT);
        }
        user.updatePhonenumber(request.getPhoneNumber());

        // 비밀번호 수정
        if (StringUtils.hasText(request.getCurrentPassword()) || StringUtils.hasText(request.getNewPassword())) {
            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                throw new BusinessException(ErrorCode.INVALID_CURRENT_PASSWORD);
            }
            if (!PasswordValidator.isValid(request.getNewPassword())) {
                throw new BusinessException(ErrorCode.INVALID_PASSWORD_FORMAT);
            }
            user.setEncodedPassword(passwordEncoder.encode(request.getNewPassword()));
        }

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

    @Transactional
    public void updateMentorCoffeechatInfo(MentorCoffeechatInfoDTO request, Long userId) {
        Mentor mentor = mentorRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MENTOR_NOT_FOUND));

        boolean isUpdated = false;

        if (request.getIsActive() == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT);
        }
        if (!request.getIsActive().equals(mentor.getIsActive())) {
            mentor.setIsActive(request.getIsActive());
            isUpdated = true;
        }

        if (request.getPreferredTime() == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT);
        }
        if (!request.getPreferredTime().equals(mentor.getPreferredTime())) {
            mentor.setPreferredTime(request.getPreferredTime());
            isUpdated = true;
        }

        Integer price = request.getPrice();
        if (price == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT);
        }
        if (price <= 0) {
            throw new BusinessException(ErrorCode.INVALID_INPUT);
        }
        if (!price.equals(mentor.getPrice())) {
            mentor.setPrice(price);
            isUpdated = true;
        }

        if (isUpdated) {
            mentor.setUpdatedAt(LocalDateTime.now());
        }
    }


    @Transactional
    public void updateMentorIntroductionInfo(MentorIntroduceInfoDTO request, Long userId) {
        Mentor mentor = mentorRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MENTOR_NOT_FOUND));

        boolean isUpdated = false;


        if (request.getIntroduction() == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT);
        }
        if (!request.getIntroduction().equals(mentor.getIntroduction())) {
            mentor.setIntroduction(request.getIntroduction());
            isUpdated = true;
        }


        if (request.getExternalLinkUrl() == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT);
        }
        if (!request.getExternalLinkUrl().equals(mentor.getExternalLinkUrl())) {
            mentor.setExternalLinkUrl(request.getExternalLinkUrl());
            isUpdated = true;
        }

        if (isUpdated) {
            mentor.setUpdatedAt(LocalDateTime.now());
        }
    }



}
