package com.newbit.column.entity;

import com.newbit.column.dto.request.CreateColumnRequestDto;
import com.newbit.column.enums.RequestType;
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

    private Boolean isApporved;

    private String updatedTitle;

    @Lob
    private String updatedContent;

    private Integer updatedPrice;

    private String updatedThumbnailUrl;

    private String rejectedReason;

    private Long adminUserId;

    private Long columnId;

    private Long mentorId;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public static ColumnRequest createdColumnRequest(CreateColumnRequestDto dto, Long mentorId) {
        return ColumnRequest.builder()
                .requestType(RequestType.CREATE)
                .isApporved(false)
                .updatedTitle(dto.getTitle())
                .updatedContent(dto.getContent())
                .updatedPrice(dto.getPrice())
                .updatedThumbnailUrl(dto.getThumbnailUrl())
                .mentorId(mentorId)
                .build();
    }

}
