package com.newbit.newbitfeatureservice.post.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "post")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@EntityListeners(AuditingEntityListener.class) // 자동 시간 관리
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    private String title;

    @Lob
    private String content;

    @Builder.Default
    @Column(name = "like_count", nullable = false)
    private int likeCount = 0;

    @Builder.Default
    @Column(name = "report_count", nullable = false)
    private int reportCount = 0;

    @Builder.Default
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "post_category_id", nullable = false)
    private Long postCategoryId;

    @Column(name = "is_notice", nullable = false)
    private boolean isNotice;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }

    public void increaseLikeCount() {
        this.likeCount++;
    }

    public void decreaseLikeCount() {
        this.likeCount = Math.max(0, this.likeCount - 1);
    }

    public void increaseReportCount() {
        this.reportCount++;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_category_id", insertable = false, updatable = false)
    private PostCategory postCategory;

}
