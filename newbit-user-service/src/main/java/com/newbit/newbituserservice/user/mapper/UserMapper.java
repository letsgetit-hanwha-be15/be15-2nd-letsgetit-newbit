package com.newbit.newbituserservice.user.mapper;

import com.newbit.newbituserservice.user.dto.request.MentorListRequestDTO;
import com.newbit.newbituserservice.user.dto.response.*;
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
    List<ReviewDTO> findReviewsByMentorId(Long mentorId);

    List<MentorListResponseDTO> findMentors(MentorListRequestDTO request);

}
