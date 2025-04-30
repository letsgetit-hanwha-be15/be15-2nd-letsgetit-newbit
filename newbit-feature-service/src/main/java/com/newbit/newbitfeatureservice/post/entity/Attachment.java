package com.newbit.newbitfeatureservice.post.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "attachment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attachment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(name = "url", nullable = false)
    private String imageUrl;

    public Attachment(Post post, String imageUrl) {
        this.post = post;
        this.imageUrl = imageUrl;
    }
}
