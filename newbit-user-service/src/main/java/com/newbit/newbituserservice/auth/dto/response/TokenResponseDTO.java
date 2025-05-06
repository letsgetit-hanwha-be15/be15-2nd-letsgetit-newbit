package com.newbit.newbituserservice.auth.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenResponseDTO {
    private Long userId;
    private String email;
    private String nickname;
    private String authority;
    private String profileImageUrl;
    private Integer point;
    private Integer diamond;
    private Long mentorId;
    private String accessToken;
    private String refreshToken;
}
