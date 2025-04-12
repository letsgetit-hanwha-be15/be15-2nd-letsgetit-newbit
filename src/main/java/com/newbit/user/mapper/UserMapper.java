package com.newbit.user.mapper;

import com.newbit.user.dto.response.OhterUserProfileDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    OhterUserProfileDTO getOhterUserProfile(@Param("userId") Long userId);
}
