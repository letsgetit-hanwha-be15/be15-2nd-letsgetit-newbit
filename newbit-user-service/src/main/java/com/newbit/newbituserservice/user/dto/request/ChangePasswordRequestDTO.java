package com.newbit.newbituserservice.user.dto.request;

import lombok.Getter;

@Getter
public class ChangePasswordRequestDTO {
    private String currentPassword;
    private String newPassword;
}
