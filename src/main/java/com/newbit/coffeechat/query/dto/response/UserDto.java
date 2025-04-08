package com.newbit.coffeechat.query.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class UserDto {
    private Long userId;
    private String nickname;
    private Authority authority;
    private String profileImageUrl;
}
