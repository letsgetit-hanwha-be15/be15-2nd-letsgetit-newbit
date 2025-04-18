package com.newbit.newbituserservice.user.dto.response;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewDTO {
    private Long reviewId;
    private Long userId;
    private Long userName;
    private double rating;
    private String comment;
    private LocalDateTime createdAt;
}
