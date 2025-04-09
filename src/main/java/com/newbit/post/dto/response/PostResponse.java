package com.newbit.post.dto.response;

import com.newbit.post.entity.Post;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostResponse {

    private final Long id;
    private final String title;
    private final String content;
    private final Long userId;
    private final Long postCategoryId;
    private final int likeCount;
    private final int reportCount;
    private final LocalDateTime createdAt;

    public PostResponse(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.userId = post.getUserId();
        this.postCategoryId = post.getPostCategoryId();
        this.likeCount = post.getLikeCount();
        this.reportCount = post.getReportCount();
        this.createdAt = post.getCreatedAt();
    }
}
