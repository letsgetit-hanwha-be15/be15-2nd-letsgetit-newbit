package com.newbit.newbituserservice.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MentorListResponseDTO {
    private Long mentorId;
    private String userName;
    private String nickname;
    private String profileImageUrl;
    private Integer price;
    private Double temperature;
    private String techstackName;
}