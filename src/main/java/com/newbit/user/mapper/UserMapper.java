package com.newbit.user.mapper;

import com.newbit.user.dto.response.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
    OhterUserProfileDTO getOhterUserProfile(@Param("userId") Long userId);

    MentorProfileDTO findMentorProfile(@Param("mentorId") Long mentorId);

    List<PostDTO> findUserPosts(@Param("userId") Long userId);
    List<PostDTO> findMentorPosts(Long mentorId);
    List<ColumnDTO> findMentorColumns(Long mentorId);
    List<SeriesDTO> findMentorSeries(Long mentorId);

}
