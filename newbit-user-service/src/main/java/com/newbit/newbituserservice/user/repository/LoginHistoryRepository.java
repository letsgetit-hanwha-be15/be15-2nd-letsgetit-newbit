package com.newbit.newbituserservice.user.repository;

import com.newbit.newbituserservice.user.entity.LoginHistory;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Long> {

    @Query("SELECT COUNT(l) FROM LoginHistory l WHERE l.userId = :userId AND FUNCTION('DATE', l.createdAt) = CURRENT_DATE")
    long countByUserIdAndToday(@Param("userId") Long userId);
}