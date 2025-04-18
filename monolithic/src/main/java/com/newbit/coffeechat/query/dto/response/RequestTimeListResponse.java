package com.newbit.coffeechat.query.dto.response;

import com.newbit.common.dto.Pagination;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RequestTimeListResponse {
    private final List<RequestTimeDto> requestTimes;
}
