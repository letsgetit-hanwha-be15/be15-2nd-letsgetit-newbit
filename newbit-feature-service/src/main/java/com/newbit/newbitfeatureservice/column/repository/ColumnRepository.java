package com.newbit.newbitfeatureservice.column.repository;

import com.newbit.newbitfeatureservice.column.domain.Column;
import com.newbit.newbitfeatureservice.column.dto.response.GetColumnDetailResponseDto;
import com.newbit.newbitfeatureservice.column.dto.response.GetColumnListResponseDto;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ColumnRepository extends JpaRepository<Column, Long> {

    // 공개 칼럼 리스트 조회 (페이징)
    @Query("""
        SELECT new com.newbit.newbitfeatureservice.column.dto.response.GetColumnListResponseDto(
            c.columnId, c.title, c.thumbnailUrl, c.price, c.likeCount, c.mentorId
        )
        FROM Column c
        WHERE c.isPublic = true
        ORDER BY c.createdAt DESC
    """)
    Page<GetColumnListResponseDto> findAllByIsPublicTrueOrderByCreatedAtDesc(Pageable pageable);

    @Query("""
    SELECT new com.newbit.newbitfeatureservice.column.dto.response.GetColumnDetailResponseDto(
        c.columnId, c.title, c.content, c.price, c.thumbnailUrl, c.likeCount, c.mentorId
    )
    FROM Column c
    WHERE c.columnId = :columnId AND c.isPublic = true
    """)
    Optional<GetColumnDetailResponseDto> findPublicColumnDetailById(@Param("columnId") Long columnId);

    // 멘토가 작성한 공개 칼럼 목록 조회 (페이징 적용)
    Page<Column> findAllByMentorIdAndIsPublicTrueOrderByCreatedAtDesc(Long mentorId, Pageable pageable);

    List<Column> findAllBySeries_SeriesId(Long seriesId);

    // 시리즈에 속한 칼럼 목록 조회 (페이징 적용)
    Page<Column> findAllBySeries_SeriesId(Long seriesId, Pageable pageable);
}
