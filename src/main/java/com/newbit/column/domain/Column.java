package com.newbit.column.domain;

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

    private Long mentorId;

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

    public void markAsDeleted() {
        this.deletedAt = LocalDateTime.now();
    }
}
