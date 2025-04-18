package com.newbit.newbitfeatureservice.column.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "`column`")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Column {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long columnId;

    private String title;

    @Lob
    private String content;

    @jakarta.persistence.Column(nullable = false)
    @Builder.Default
    private boolean isPublic = false;

    @jakarta.persistence.Column(nullable = false)
    private int price;

    private int likeCount;

    private String thumbnailUrl;

    @CreatedDate
    @jakarta.persistence.Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @jakarta.persistence.Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @jakarta.persistence.Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "column", cascade = CascadeType.ALL)
    @Builder.Default
    private List<ColumnRequest> requests = new ArrayList<>();

    @jakarta.persistence.Column(name = "mentor_id")
    private Long mentorId;

    @ManyToOne
    @JoinColumn(name = "series_id")
    private Series series;


    public void markAsDeleted() {
        this.deletedAt = LocalDateTime.now();
    }

    public void approve() {
        this.isPublic = true;
    }

    public void updateSeries(Series series) {
        this.series = series;
    }

    public void updateContent(String title, String content, Integer price, String thumbnailUrl) {
        if (title != null) this.title = title;
        if (content != null) this.content = content;
        if (price != null) this.price = price;
        if (thumbnailUrl != null) this.thumbnailUrl = thumbnailUrl;
    }

    // 좋아요 카운트 증가
    public void increaseLikeCount() {
        this.likeCount += 1;
    }
    
    // 좋아요 카운트 감소
    public void decreaseLikeCount() {
        this.likeCount = Math.max(0, this.likeCount - 1);
    }
}
