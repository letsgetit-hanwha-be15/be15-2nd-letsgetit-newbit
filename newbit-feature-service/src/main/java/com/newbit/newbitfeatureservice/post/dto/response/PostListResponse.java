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
    private final String writerNickname;
    private final Long categoryId;
    private final Integer likeCount;
    private final String serialNumber;
    private final Long userId;

    public PostListResponse(Post post, String writerNickname, String serialNumber) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.isNotice = post.isNotice();
        this.createdAt = post.getCreatedAt();
        this.writerNickname = writerNickname;
        this.categoryId = post.getPostCategoryId();
        this.likeCount = post.getLikeCount();
        this.serialNumber = serialNumber;
        this.userId = post.getUserId();
    }
}
