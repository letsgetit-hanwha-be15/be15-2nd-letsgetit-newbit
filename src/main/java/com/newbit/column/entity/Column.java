package com.newbit.column.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
    private boolean isPublic = false;

    @jakarta.persistence.Column(nullable = false)
    private int price;

    private int likeCount;

    private String thumbnailUrl;

    private Long mentorId;

    @OneToMany(mappedBy = "column", cascade = CascadeType.ALL)
    private List<ColumnRequest> requests = new ArrayList<>();
}
