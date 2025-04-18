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
    private String accessToken;
    private String profileImageUrl;
    private String refreshToken;
}
