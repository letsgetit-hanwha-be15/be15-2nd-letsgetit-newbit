package com.newbit.user.service;

import com.newbit.common.exception.BusinessException;
import com.newbit.common.exception.ErrorCode;
import com.newbit.user.dto.request.MentorListRequestDTO;
import com.newbit.user.dto.response.*;
import com.newbit.user.mapper.UserMapper;
import com.newbit.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserQueryService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;

    public OtherUserProfileDTO getOtherUserProfile(Long userId) {
        OtherUserProfileDTO profile = userMapper.getOtherUserProfile(userId);
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

        List<PostDTO> posts = userMapper.findMentorPosts(mentorId);
        List<ColumnDTO> columns = userMapper.findMentorColumns(mentorId);
        List<SeriesDTO> series = userMapper.findMentorSeries(mentorId);
        List<ReviewDTO> reviews = userMapper.findReviewsByMentorId(mentorId);

        profile.setPosts(posts);
        profile.setColumns(columns);
        profile.setSeries(series);
        profile.setReviews(reviews);

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
