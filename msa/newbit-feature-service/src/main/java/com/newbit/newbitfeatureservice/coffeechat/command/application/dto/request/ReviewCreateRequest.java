package com.newbit.newbitfeatureservice.coffeechat.command.application.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReviewCreateRequest {
    @NotNull
    @Min(value = 1)
    @Max(value = 5)
    private final double rating;
    @Size(min = 50, message = "리뷰내용은 최소 50자 이상이어야 합니다.")
    private final String comment;
    @Min(value = 1)
    private final Integer tip;
    @Min(value = 1)
    private final Long coffeechatId;
}
