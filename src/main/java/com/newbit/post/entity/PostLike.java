package com.newbit.post.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class PostLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username; // 좋아요 누른 사용자 (실제 서비스에서는 userId로 바꾸는 것이 일반적)

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;
}
