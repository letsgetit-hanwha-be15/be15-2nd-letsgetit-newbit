package com.newbit.column.repository;

import com.newbit.column.domain.Column;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ColumnRepository extends JpaRepository<Column, Long> {
    List<Column> findAllByIsPublicTrueOrderByCreatedAtDesc();
}
