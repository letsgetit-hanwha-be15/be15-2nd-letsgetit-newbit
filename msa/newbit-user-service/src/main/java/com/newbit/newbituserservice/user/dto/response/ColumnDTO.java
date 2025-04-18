package com.newbit.newbituserservice.user.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ColumnDTO {
    private Long columnId;
    private String title;
    private String nickname;     // 작성자
    private LocalDateTime createdAt;
}
