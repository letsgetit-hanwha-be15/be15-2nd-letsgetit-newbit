package com.newbit.newbitfeatureservice.post.repository;

import com.newbit.newbitfeatureservice.post.entity.Comment;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostIdAndDeletedAtIsNull(Long postId);

    Page<Comment> findByPostIdAndDeletedAtIsNull(Long postId, Pageable pageable);

    @Query("SELECT COALESCE(SUM(c.reportCount), 0) FROM Comment c WHERE c.userId = :userId")
    int sumReportCountByUserId(@Param("userId") Long userId);


}
