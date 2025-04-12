package com.newbit.like.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.newbit.like.entity.Like;

public interface LikeRepository extends JpaRepository<Like, Long> {
    
    Optional<Like> findByPostIdAndUserIdAndIsDeleteFalse(Long postId, Long userId);
    
    boolean existsByPostIdAndUserIdAndIsDeleteFalse(Long postId, Long userId);
    
    int countByPostIdAndUserId(Long postId, Long userId);
    
    int countByPostIdAndIsDeleteFalse(Long postId);
} 