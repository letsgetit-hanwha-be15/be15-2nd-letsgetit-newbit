package com.newbit.newbitfeatureservice.coffeechat.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Setter
@ToString
@Schema(description = "커피챗 목록 DTO")
public class CoffeechatListDto {
    @Schema(description = "커피챗ID")
    private Long coffeechatId;
    @Schema(description = "진행상태")
    private ProgressStatus progressStatus;
    @Schema(description = "요청메시지")
    private String requestMessage;
    @Schema(description = "유저 프로필")
    private String profileImageUrl;
    @Schema(description = "유저 닉네임")
    private String nickname;
}