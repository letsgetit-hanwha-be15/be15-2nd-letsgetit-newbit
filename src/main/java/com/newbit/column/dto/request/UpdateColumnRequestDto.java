package com.newbit.column.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateColumnRequestDto {
    private String title;
    private String content;
    private Integer price;
    private String thumbnailUrl;
}