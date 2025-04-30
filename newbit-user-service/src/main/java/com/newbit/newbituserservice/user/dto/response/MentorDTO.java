package com.newbit.newbituserservice.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Setter
@ToString
@Schema(description = "멘토 DTO")
public class MentorDTO {
    private Boolean isActive;
    private Integer price;
    private String preferredTime;
    private BigDecimal temperature;
    private String nickname;
    private String profileImageUrl;
    private String jobName;
    private String introduction;
    private String externalLinkUrl;
}
