package com.newbit.coffeechat.query.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    private Long userId;
    private String nickname;
    private Authority authority;
    private String profileImageUrl;
}
