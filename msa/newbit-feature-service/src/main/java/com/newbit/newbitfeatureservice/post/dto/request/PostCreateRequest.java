package com.newbit.newbitfeatureservice.post.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostCreateRequest {
    private String title;
    private String content;
    private Long postCategoryId;
}
