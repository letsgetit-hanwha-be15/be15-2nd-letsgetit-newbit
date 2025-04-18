package com.newbit.newbituserservice.user.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostDTO {
    private Long postId;
    private String title;
    private String nickname;
    private LocalDateTime createdAt;
}
