package com.newbit.newbituserservice.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UserInfoUpdateRequestDTO {

    @NotBlank(message = "전화번호는 필수입니다.")
    private String phoneNumber;

    private String currentPassword;
    private String newPassword;
}
