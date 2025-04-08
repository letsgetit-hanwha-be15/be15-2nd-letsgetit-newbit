package com.newbit.user.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserRequestDTO {
    private final String email;
    private final String password;
    private final String phoneNumber;
    private final String userName;
    private final String nickname;
    private final String profileImgUrl;
    // 추가 회원 가입 시 필요한 데이터
}
