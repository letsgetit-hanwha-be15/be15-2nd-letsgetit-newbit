package com.newbit.newbitfeatureservice.post.dto.response;

import com.newbit.newbitfeatureservice.post.entity.Comment;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentResponse {

    private final Long id;
    private final String content;
    private final int reportCount;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final Long userId;
    private final Long postId;

    public CommentResponse(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.reportCount = comment.getReportCount();
        this.createdAt = comment.getCreatedAt();
        this.updatedAt = comment.getUpdatedAt();
        this.userId = comment.getUserId();
        this.postId = comment.getPost().getId();
    }
}
