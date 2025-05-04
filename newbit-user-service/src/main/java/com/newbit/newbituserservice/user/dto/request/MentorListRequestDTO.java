package com.newbit.newbituserservice.user.dto.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MentorListRequestDTO {
    private String techstackName;
    private String userName;
    private String coffeechatPrice; // "asc" or "desc"
    private String temperature;     // "asc" or "desc"

    private int page;   // 클라이언트로부터 받은 페이지 번호 (1부터 시작)
    private int size;   // 한 페이지에 보여줄 mentor 수

    // 내부 계산용
    private int offset;
    private int limit;
}