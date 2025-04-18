package com.newbit.newbitfeatureservice.post.dto.response;

import com.newbit.newbitfeatureservice.post.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class PostDetailResponse {
    private Long id;
    private String title;
    private String content;
    private int likeCount;
    private final LocalDateTime createdAt;
    private String writerName;
    private String categoryName;
    private List<CommentResponse> comments;

    public PostDetailResponse(Post post, List<CommentResponse> comments, String writerName, String categoryName) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.likeCount = post.getLikeCount();
        this.createdAt = post.getCreatedAt();
        this.writerName = writerName;
        this.categoryName = categoryName;
        this.comments = comments;
    }
}
