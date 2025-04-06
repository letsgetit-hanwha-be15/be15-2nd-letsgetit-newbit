package com.newbit.board.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.security.Timestamp;

@Entity
@Data
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    private String content;

    @Column(name = "report_count")
    private int reportCount;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "deleted_at")
    private Timestamp deletedAt;

    @Column(name = "user_id") // ← writer 필드지만 실제 DB는 user_id 컬럼
    private String writer;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Board board;
}
