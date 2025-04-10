package com.newbit.column.repository;

import com.newbit.column.domain.Column;
import com.newbit.column.dto.response.GetColumnListResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ColumnRepository extends JpaRepository<Column, Long> {

    @Query("SELECT new com.newbit.column.dto.response.GetColumnListResponseDto( " +
            "c.columnId, c.title, c.thumbnailUrl, c.price, c.likeCount, m.mentorId, u.nickname) " +
            "FROM Column c " +
            "JOIN c.mentor m " +
            "JOIN m.user u " +
            "WHERE c.isPublic = true " +
            "ORDER BY c.createdAt DESC")

    Page<GetColumnListResponseDto> findAllByIsPublicTrueOrderByCreatedAtDesc(Pageable pageable);
}
