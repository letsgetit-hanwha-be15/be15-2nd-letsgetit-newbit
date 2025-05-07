package com.newbit.newbitfeatureservice.column.repository;

import com.newbit.newbitfeatureservice.column.domain.ColumnRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ColumnRequestRepository extends JpaRepository<ColumnRequest, Long> {

    Page<ColumnRequest> findAllByColumn_MentorIdOrderByCreatedAtDesc(Long mentorId, Pageable pageable);
}