package com.newbit.post.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Data
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    private String content;

    @Column(name = "report_count")
    private int reportCount = 0;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // 사용자 - 외래 키 연결 (User 엔티티가 있을 경우)
    // @ManyToOne
    // @JoinColumn(name = "user_id")
    // private User user;

    @Column(name = "writer")
    private String writer;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;
}
