package com.newbit.coffeechat.command.application.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReviewCommandResponse {
    private final Long reviewId;
}
