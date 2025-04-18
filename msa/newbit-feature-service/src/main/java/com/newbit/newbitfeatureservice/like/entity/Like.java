package com.newbit.newbitfeatureservice.like.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "`like`")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@EntityListeners(AuditingEntityListener.class)
@Schema(description = "좋아요 엔티티")
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id")
    @Schema(description = "좋아요 ID", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    @Schema(description = "사용자 ID")
    private Long userId;

    @Column(name = "post_id")
    @Schema(description = "게시글 ID")
    private Long postId;

    @Column(name = "column_id")
    @Schema(description = "컬럼 ID (컬럼 좋아요 기능 위한 필드)")
    private Long columnId;

    @Builder.Default
    @Column(name = "is_delete", nullable = false)
    @Schema(description = "삭제 여부 (true: 삭제됨, false: 활성화)")
    private boolean isDelete = false;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    @Schema(description = "생성 시간", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    @Schema(description = "수정 시간", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;

    public void markAsDeleted() {
        this.isDelete = true;
    }
    
    public void cancelDelete() {
        this.isDelete = false;
    }
} 