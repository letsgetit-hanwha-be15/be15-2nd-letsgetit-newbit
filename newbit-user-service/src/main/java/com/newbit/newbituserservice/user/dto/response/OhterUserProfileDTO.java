package com.newbit.newbituserservice.user.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class OhterUserProfileDTO {
    private String userName;
    private String nickname;
    private String profileImageUrl;
    private String jobName;

    private List<PostDTO> posts;
}
