package com.newbit.post.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    private String title;

    private String content;

    @Column(name = "like_count")
    private int likeCount = 0;

    @Column(name = "report_count")
    private int reportCount = 0;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "post_category_id", nullable = false)
    private Long postCategoryId;

    @Column(name = "filename")
    private String filename;

    @Column(name = "filepath")
    private String filepath;
}
