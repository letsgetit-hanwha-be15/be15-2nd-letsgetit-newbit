package com.newbit.newbituserservice.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UserInfoUpdateRequestDTO {
    @NotBlank(message = "닉네임은 필수입니다.")
    private String nickname;

    @NotBlank(message = "이름은 필수입니다.")
    private String jobId;

    @NotBlank(message = "전화번호는 필수입니다.")
    private String phoneNumber;

    private String profileImageUrl;

}
