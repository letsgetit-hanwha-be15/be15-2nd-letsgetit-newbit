package com.newbit.newbitfeatureservice.column.repository;

import com.newbit.newbitfeatureservice.column.domain.Series;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SeriesRepository extends JpaRepository<Series, Long> {
    // 멘토가 작성한 시리즈 전체 조회 (본인용)
    Page<Series> findAllByMentorIdOrderByCreatedAtDesc(Long mentorId, Pageable pageable);

    // 칼럼이 1개 이상 포함된 시리즈만 조회 (공개용, 페이징 처리)
    @Query("""
        SELECT s FROM Series s
        WHERE size(s.columns) > 0
        ORDER BY s.createdAt DESC
    """)
    Page<Series> findAllByColumnsIsNotEmpty(Pageable pageable);
}
