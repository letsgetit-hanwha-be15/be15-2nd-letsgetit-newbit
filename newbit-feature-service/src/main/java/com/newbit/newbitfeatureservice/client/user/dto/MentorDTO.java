package com.newbit.newbitfeatureservice.client.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Setter
@ToString
@Schema(description = "멘토 DTO")
public class MentorDTO {
    Boolean isActive;
    Integer price;
}
