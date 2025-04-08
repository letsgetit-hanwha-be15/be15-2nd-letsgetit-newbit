package com.newbit.coffeechat.query.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CoffeechatSearchRequest {
    private Integer page = 1;
    private Integer size = 10;
    private Long mentorId;
    private Long menteeId;

    public int getOffset() {
        return (page - 1) * size;
    }

    public int getLimit() {
        return size;
    }
}