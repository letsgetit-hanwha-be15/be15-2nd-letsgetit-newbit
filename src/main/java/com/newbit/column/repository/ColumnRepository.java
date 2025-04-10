package com.newbit.column.repository;

import com.newbit.column.domain.Column;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ColumnRepository extends JpaRepository<Column, Long> {
    Page<Column> findAllByIsPublicTrueOrderByCreatedAtDesc(Pageable pageable);
}
