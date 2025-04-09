package com.newbit.post.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostCreateRequest {
    private String title;
    private String content;
    private Long userId;
    private Long postCategoryId;
}
