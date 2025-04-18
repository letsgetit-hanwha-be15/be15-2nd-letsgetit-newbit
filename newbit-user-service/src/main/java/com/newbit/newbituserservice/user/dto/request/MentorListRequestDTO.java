package com.newbit.newbituserservice.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MentorListRequestDTO {
    private String coffeechatPrice;   // "ASC" or "DESC"
    private String temperature;       // "ASC" or "DESC"
    private String techstackName;
    private String userName;

    private int page = 0;    // 기본값
    private int size = 15;   // 기본값
}
