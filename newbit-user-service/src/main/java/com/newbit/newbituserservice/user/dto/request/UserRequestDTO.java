package com.newbit.newbituserservice.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserRequestDTO {

    @NotNull
    @Schema(description = "사용자 ID(email)")
    private final String email;
    @NotNull
    @Schema(description = "사용자 Password")
    private final String password;
    @NotNull
    @Schema(description = "사용자 핸드폰 번호")
    private final String phoneNumber;
    @NotNull
    @Schema(description = "사용자 이름")
    private final String userName;
    @NotNull
    @Schema(description = "사용자 닉네임")
    private final String nickname;

    @Schema(description = "사용자 프로필 url")
    private final String profileImgUrl;
    // 추가 회원 가입 시 필요한 데이터
}
