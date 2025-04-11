package com.newbit.column.repository;

import com.newbit.column.domain.Series;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeriesRepository extends JpaRepository<Series, Long> {
}
