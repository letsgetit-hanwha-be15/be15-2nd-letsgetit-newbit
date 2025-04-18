package com.newbit.user.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class OtherUserProfileDTO {
    private String userName;
    private String nickname;
    private String profileImageUrl;
    private String jobName;

    private List<PostDTO> posts;
}
