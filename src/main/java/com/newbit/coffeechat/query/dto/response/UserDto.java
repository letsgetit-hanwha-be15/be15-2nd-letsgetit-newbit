package com.newbit.coffeechat.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Setter
@ToString
@Schema(description = "회원 정보 DTO")
public class UserDto {
    @Schema(description = "회원번호")
    private Long userId;
    @Schema(description = "닉네임")
    private String nickname;
    @Schema(description = "권한")
    private Authority authority;
    @Schema(description = "프로필 이미지 URL")
    private String profileImageUrl;
}
