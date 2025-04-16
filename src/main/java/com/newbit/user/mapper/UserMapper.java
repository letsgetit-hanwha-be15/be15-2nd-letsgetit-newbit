package com.newbit.user.mapper;

import com.newbit.user.dto.response.OtherUserProfileDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    OtherUserProfileDTO getOtherUserProfile(@Param("userId") Long userId);
}
