package com.newbit.newbitfeatureservice.column.repository;

import com.newbit.newbitfeatureservice.column.domain.Series;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeriesRepository extends JpaRepository<Series, Long> {
    List<Series> findAllByMentorIdOrderByCreatedAtDesc(Long mentorId);
}
