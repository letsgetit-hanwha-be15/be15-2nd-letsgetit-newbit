package com.newbit.newbituserservice.user.dto.response;


import com.newbit.newbituserservice.user.entity.Authority;
import com.newbit.newbituserservice.user.entity.User;
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

    public static UserDTO fromEntity(User user) {
        return UserDTO.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .userName(user.getUserName())
                .nickname(user.getNickname())
                .point(user.getPoint())
                .diamond(user.getDiamond())
                .authority(user.getAuthority())
                .profileImageUrl(user.getProfileImageUrl())
                .jobId((user.getJobId() != null) ? user.getJobId().toString() : null)
                .build();
    }
}
