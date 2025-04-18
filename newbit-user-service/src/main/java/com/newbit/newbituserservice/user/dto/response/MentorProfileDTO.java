package com.newbit.newbituserservice.user.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class MentorProfileDTO {
    private String userName;
    private String nickname;
    private String profileImageUrl;
    private String jobName;
    private Integer temperature;
    private String preferredTime;
    private String techstackName;
    private Boolean isActive;
    private Integer price;

    private List<PostDTO> posts;
    private List<ColumnDTO> columns;
    private List<SeriesDTO> series;
    private List<ReviewDTO> reviews;
}
