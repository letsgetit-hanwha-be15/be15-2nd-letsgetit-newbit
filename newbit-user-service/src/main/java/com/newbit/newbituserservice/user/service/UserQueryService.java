package com.newbit.newbituserservice.user.service;


import com.newbit.newbituserservice.common.dto.Pagination;
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

    public MentorListResponseWrapper getMentors(MentorListRequestDTO request) {
        // 페이지와 크기 기본값 보정 (1페이지 이상, 1개 이상)
        int page = Math.max(request.getPage(), 1);
        int size = Math.max(request.getSize(), 1);

        // offset 및 limit 설정
        request.setOffset((page - 1) * size);
        request.setLimit(size);

        // 멘토 목록 조회
        List<MentorListResponseDTO> mentors = userMapper.findMentors(request);

        // 총 멘토 수 조회
        long total = userMapper.countMentors(request);

        // 응답 객체 조립
        return MentorListResponseWrapper.builder()
                .mentors(mentors)
                .pagination(Pagination.builder()
                        .currentPage(page)
                        .totalPage((int) Math.ceil((double) total / size))
                        .totalItems(total)
                        .build())
                .build();
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
