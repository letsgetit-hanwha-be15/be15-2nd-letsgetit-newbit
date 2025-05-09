package com.newbit.newbitfeatureservice.post.dto.response;

import com.newbit.newbitfeatureservice.post.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class PostDetailResponse {
    private Long id;
    private String title;
    private String content;
    private int likeCount;
    private int reportCount;
    private boolean isNotice;
    private final LocalDateTime createdAt;
    private String writerName;
    private String categoryName;
    private List<String> imageUrls;
    private List<CommentResponse> comments;
    private Long userId;

    public PostDetailResponse(Post post, List<CommentResponse> comments, String writerName, String categoryName) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.likeCount = post.getLikeCount();
        this.reportCount = post.getReportCount();
        this.createdAt = post.getCreatedAt();
        this.writerName = writerName;
        this.categoryName = categoryName;
        this.imageUrls = post.getImageUrls();
        this.isNotice = post.isNotice();
        this.comments = comments;
        this.userId = post.getUserId();
    }
}
