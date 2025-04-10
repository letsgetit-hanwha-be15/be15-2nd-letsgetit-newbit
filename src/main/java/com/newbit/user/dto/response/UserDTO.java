package com.newbit.user.dto.response;

import com.newbit.user.entity.Authority;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Setter
@ToString
@Schema(description = "회원 DTO")
public class UserDTO {
    Long userId;
    Authority authority;
    Integer diamond;
    Integer point;
}
