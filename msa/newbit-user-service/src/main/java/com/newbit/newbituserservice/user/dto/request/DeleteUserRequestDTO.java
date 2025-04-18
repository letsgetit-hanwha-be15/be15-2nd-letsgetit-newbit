package com.newbit.newbituserservice.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class DeleteUserRequestDTO {

    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;
}
