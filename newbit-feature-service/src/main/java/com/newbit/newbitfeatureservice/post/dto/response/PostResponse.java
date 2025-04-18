package com.newbit.newbitfeatureservice.post.dto.response;

import com.newbit.newbitfeatureservice.post.entity.Post;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostResponse {

    private final Long id;
    private final String title;
    private final String content;
    private final int likeCount;
    private final int reportCount;
    private final boolean isNotice;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final LocalDateTime deletedAt;
    private final Long userId;
    private final Long postCategoryId;

    public PostResponse(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.likeCount = post.getLikeCount();
        this.reportCount = post.getReportCount();
        this.isNotice = post.isNotice();
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
        this.deletedAt = post.getDeletedAt();
        this.userId = post.getUserId();
        this.postCategoryId = post.getPostCategoryId();
    }
}
