package com.newbit.user.service;

import com.newbit.common.exception.BusinessException;
import com.newbit.common.exception.ErrorCode;
import com.newbit.user.dto.response.*;
import com.newbit.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserQueryService {

    private final UserMapper userMapper;

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

        List<PostDTO> posts = userMapper.findMentorPosts(mentorId);
        List<ColumnDTO> columns = userMapper.findMentorColumns(mentorId);
        List<SeriesDTO> series = userMapper.findMentorSeries(mentorId);

        profile.setPosts(posts);
        profile.setColumns(columns);
        profile.setSeries(series);

        return profile;
    }
}
