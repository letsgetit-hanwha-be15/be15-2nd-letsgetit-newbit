package com.newbit.newbitfeatureservice.column.domain;

import com.newbit.newbitfeatureservice.column.enums.RequestType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "column_request")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class ColumnRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long columnRequestId;

    @Enumerated(EnumType.STRING)
    private RequestType requestType;

    private Boolean isApproved;

    private String updatedTitle;

    @Lob
    private String updatedContent;

    private Integer updatedPrice;

    private String updatedThumbnailUrl;

    private String rejectedReason;

    private Long adminUserId;

    @ManyToOne
    @JoinColumn(name = "column_id", nullable = false)
    private Column column;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public void approve(Long adminUserId) {
        this.isApproved = true;
        this.adminUserId = adminUserId;
        this.getColumn().approve();
    }

    public void reject(String reason, Long adminUserId) {
        this.isApproved = false;
        this.rejectedReason = reason;
        this.adminUserId = adminUserId;
    }


    /* 현재 사용하지 않음. 추후 테스트/기타 로직에 활용 가능 */
//    public static ColumnRequest createdColumnRequest(CreateColumnRequestDto dto, Column column) {
//        return ColumnRequest.builder()
//                .requestType(RequestType.CREATE)
//                .isApproved(false)
//                .updatedTitle(dto.getTitle())
//                .updatedContent(dto.getContent())
//                .updatedPrice(dto.getPrice())
//                .updatedThumbnailUrl(dto.getThumbnailUrl())
//                .column(column)
//                .build();
//    }

}
