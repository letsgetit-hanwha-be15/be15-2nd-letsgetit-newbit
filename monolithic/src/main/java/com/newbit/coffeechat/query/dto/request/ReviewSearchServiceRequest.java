package com.newbit.coffeechat.query.dto.request;

import com.newbit.coffeechat.command.domain.aggregate.ProgressStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewSearchServiceRequest {
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
