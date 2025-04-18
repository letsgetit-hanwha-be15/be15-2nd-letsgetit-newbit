package com.newbit.newbitfeatureservice.coffeechat.query.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RequestTimeListResponse {
    private final List<RequestTimeDto> requestTimes;
}
