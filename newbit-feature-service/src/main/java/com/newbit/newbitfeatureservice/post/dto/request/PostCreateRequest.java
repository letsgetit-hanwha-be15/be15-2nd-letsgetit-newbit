package com.newbit.newbitfeatureservice.post.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PostCreateRequest {
    private String title;
    private String content;
    private Long postCategoryId;
    private List<String> imageUrls;
    private boolean isNotice;
}
