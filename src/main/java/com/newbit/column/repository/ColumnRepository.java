package com.newbit.column.repository;

import com.newbit.column.domain.Column;
import com.newbit.column.dto.response.GetColumnDetailResponseDto;
import com.newbit.column.dto.response.GetColumnListResponseDto;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ColumnRepository extends JpaRepository<Column, Long> {

    @Query("SELECT new com.newbit.column.dto.response.GetColumnListResponseDto( " +
            "c.columnId, c.title, c.thumbnailUrl, c.price, c.likeCount, m.mentorId, u.nickname) " +
            "FROM Column c " +
            "JOIN c.mentor m " +
            "JOIN m.user u " +
            "WHERE c.isPublic = true " +
            "ORDER BY c.createdAt DESC")

    Page<GetColumnListResponseDto> findAllByIsPublicTrueOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT new com.newbit.column.dto.response.GetColumnDetailResponseDto( " +
            "c.columnId, c.title, c.content, c.price, c.thumbnailUrl, c.likeCount, m.mentorId, u.nickname) " +
            "FROM Column c " +
            "JOIN c.mentor m " +
            "JOIN m.user u " +
            "WHERE c.columnId = :columnId AND c.isPublic = true")
    Optional<GetColumnDetailResponseDto> findPublicColumnDetailById(@Param("columnId") Long columnId);

    List<Column> findAllByMentor_MentorIdAndIsPublicTrueOrderByCreatedAtDesc(Long mentorId);

}
