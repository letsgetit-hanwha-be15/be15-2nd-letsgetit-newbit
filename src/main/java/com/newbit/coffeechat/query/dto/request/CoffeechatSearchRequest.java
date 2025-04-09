package com.newbit.coffeechat.query.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CoffeechatSearchRequest {
    private Integer page = 1;
    private Integer size = 10;
    private Long mentorId; // TODO : 회원가입 기능 생기면 삭제
    private Long menteeId; // TODO : 회원가입 기능 생기면 삭제

    public int getOffset() {
        return (page - 1) * size;
    }

    public int getLimit() {
        return size;
    }
}