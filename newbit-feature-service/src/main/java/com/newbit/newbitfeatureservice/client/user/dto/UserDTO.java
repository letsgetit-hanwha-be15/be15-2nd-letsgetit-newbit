package com.newbit.newbitfeatureservice.client.user.dto;

import com.newbit.newbitfeatureservice.coffeechat.query.dto.response.Authority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserDTO {
    private Long userId;
    private String email;
    private String phoneNumber;
    private String userName;
    private String nickname;
    private Integer point;
    private Integer diamond;
    private Authority authority;
    private String profileImageUrl;
    private String jobId;
}