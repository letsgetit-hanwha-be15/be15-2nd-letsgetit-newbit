package com.newbit.newbitfeatureservice.coffeechat.query.dto.response;

import com.newbit.newbitfeatureservice.common.dto.Pagination;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CoffeechatListResponse {
    private final List<CoffeechatListDto> coffeechats;
    private final Pagination pagination;
}
