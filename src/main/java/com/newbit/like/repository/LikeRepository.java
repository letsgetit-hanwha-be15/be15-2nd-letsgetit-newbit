package com.newbit.like.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.newbit.like.entity.Like;

public interface LikeRepository extends JpaRepository<Like, Long> {
    
    // 게시글 좋아요 관련 메서드
    @Query("SELECT l FROM Like l WHERE l.postId = :postId AND l.userId = :userId AND l.isDelete = false")
    Optional<Like> findByPostIdAndUserIdAndIsDeleteFalse(
            @Param("postId") Long postId, 
            @Param("userId") Long userId);
    
    @Query("SELECT COUNT(l) > 0 FROM Like l WHERE l.postId = :postId AND l.userId = :userId AND l.isDelete = false")
    boolean existsByPostIdAndUserIdAndIsDeleteFalse(
            @Param("postId") Long postId, 
            @Param("userId") Long userId);
    
    @Query("SELECT COUNT(l) FROM Like l WHERE l.postId = :postId AND l.userId = :userId")
    int countByPostIdAndUserId(
            @Param("postId") Long postId, 
            @Param("userId") Long userId);
    
    @Query("SELECT COUNT(l) FROM Like l WHERE l.postId = :postId AND l.isDelete = false")
    int countByPostIdAndIsDeleteFalse(@Param("postId") Long postId);
    
    // 칼럼 좋아요 관련 메서드
    @Query("SELECT l FROM Like l WHERE l.columnId = :columnId AND l.userId = :userId AND l.isDelete = false")
    Optional<Like> findByColumnIdAndUserIdAndIsDeleteFalse(
            @Param("columnId") Long columnId, 
            @Param("userId") Long userId);
    
    @Query("SELECT COUNT(l) > 0 FROM Like l WHERE l.columnId = :columnId AND l.userId = :userId AND l.isDelete = false")
    boolean existsByColumnIdAndUserIdAndIsDeleteFalse(
            @Param("columnId") Long columnId, 
            @Param("userId") Long userId);
    
    @Query("SELECT COUNT(l) FROM Like l WHERE l.columnId = :columnId AND l.isDelete = false")
    int countByColumnIdAndIsDeleteFalse(@Param("columnId") Long columnId);
} 