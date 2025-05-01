package com.newbit.newbituserservice.user.service;


import com.newbit.newbituserservice.common.exception.BusinessException;
import com.newbit.newbituserservice.common.exception.ErrorCode;
import com.newbit.newbituserservice.user.dto.request.MentorListRequestDTO;
import com.newbit.newbituserservice.user.dto.response.*;
import com.newbit.newbituserservice.user.mapper.UserMapper;
import com.newbit.newbituserservice.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserQueryService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;

    public OhterUserProfileDTO getOhterUserProfile(Long userId) {
        OhterUserProfileDTO profile = userMapper.getOhterUserProfile(userId);
        if (profile == null) {
            throw new BusinessException(ErrorCode.USER_INFO_NOT_FOUND);
        }

        List<PostDTO> userPosts = userMapper.findUserPosts(userId); //
        profile.setPosts(userPosts);

        return profile;
    }

    public MentorProfileDTO getMentorProfile(Long mentorId) {
        MentorProfileDTO profile = userMapper.findMentorProfile(mentorId);
        if (profile == null) {
            throw new BusinessException(ErrorCode.USER_INFO_NOT_FOUND);
        }

        // techstack 별도 조회
        List<String> techstacks = userMapper.findMentorTechstacks(mentorId);
        profile.setTechstackNames(techstacks);

        profile.setPosts(userMapper.findMentorPosts(mentorId));
        profile.setColumns(userMapper.findMentorColumns(mentorId));
        profile.setSeries(userMapper.findMentorSeries(mentorId));
        profile.setReviews(userMapper.findReviewsByMentorId(mentorId));

        return profile;
    }

    public List<MentorListResponseDTO> getMentors(MentorListRequestDTO request) {
        List<MentorListResponseDTO> mentors = userMapper.findMentors(request);
        if (mentors.isEmpty()) {
            throw new BusinessException(ErrorCode.MENTOR_NOT_FOUND);
        }
        return mentors;
    }

    public String getEmailByUserId(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND))
                .getEmail();
    }

    public String getNicknameByUserId(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND))
                .getNickname();
    }
}
