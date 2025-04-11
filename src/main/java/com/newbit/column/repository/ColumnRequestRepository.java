package com.newbit.column.repository;

import com.newbit.column.domain.ColumnRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ColumnRequestRepository extends JpaRepository<ColumnRequest, Long> {

    List<ColumnRequest> findAllByColumn_Mentor_MentorIdOrderByCreatedAtDesc(Long mentorId);
}