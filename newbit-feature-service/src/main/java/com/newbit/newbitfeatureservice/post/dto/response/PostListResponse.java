package com.newbit.newbitfeatureservice.post.dto.response;

import com.newbit.newbitfeatureservice.post.entity.Post;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostListResponse {

    private final Long id;
    private final String title;
    private final boolean isNotice;
    private final LocalDateTime createdAt;

    public PostListResponse(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.isNotice = post.isNotice();
        this.createdAt = post.getCreatedAt();
    }
}
