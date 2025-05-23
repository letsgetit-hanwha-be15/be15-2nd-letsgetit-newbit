package com.newbit.newbitfeatureservice.coffeechat.query.dto.request;

import com.newbit.newbitfeatureservice.coffeechat.command.domain.aggregate.ProgressStatus;
import com.newbit.newbitfeatureservice.coffeechat.query.dto.response.Authority;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CoffeechatSearchServiceRequest {
    private Integer page = 1;
    private Integer size = 10;
    private Long menteeId;
    private Long mentorId;
    private ProgressStatus progressStatus;
    private Boolean isProgressing;

    public int getOffset() {
        return (page - 1) * size;
    }

    public int getLimit() {
        return size;
    }
}