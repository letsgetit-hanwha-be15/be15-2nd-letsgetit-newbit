package com.newbit.newbituserservice.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.util.List;

@Getter
public class UserProfileInfoUpdateRequestDTO {
    @NotBlank(message = "닉네임은 필수입니다.")
    private String nickname;

    private String profileImageUrl;

    private String jobName;

    @Schema(description = "사용자 기술 스택")
    private List<String> techstackNames;
}
