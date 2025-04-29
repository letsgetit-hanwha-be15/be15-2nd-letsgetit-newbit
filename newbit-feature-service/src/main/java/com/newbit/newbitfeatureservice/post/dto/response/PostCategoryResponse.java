package com.newbit.newbitfeatureservice.post.dto.response;

import com.newbit.newbitfeatureservice.post.entity.PostCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostCategoryResponse {
    private Long id;
    private String name;

    public PostCategoryResponse(PostCategory category) {
        this.id = category.getId();
        this.name = category.getName();
    }
}
