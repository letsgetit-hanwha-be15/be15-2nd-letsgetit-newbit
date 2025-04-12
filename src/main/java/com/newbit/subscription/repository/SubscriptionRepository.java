package com.newbit.subscription.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.newbit.subscription.entity.Subscription;
import com.newbit.subscription.entity.SubscriptionId;

public interface SubscriptionRepository extends JpaRepository<Subscription, SubscriptionId> {
    
    // 특정 사용자의 모든 구독 목록 조회
    List<Subscription> findByUserId(Long userId);
    
    // 특정 시리즈의 모든 구독자 목록 조회
    List<Subscription> findBySeriesId(Long seriesId);
    
    // 사용자가 특정 시리즈를 구독 중인지 확인
    boolean existsByUserIdAndSeriesId(Long userId, Long seriesId);
    
    // 특정 시리즈의 구독자 수 조회
    int countBySeriesId(Long seriesId);
    
    // 특정 사용자의 특정 시리즈 구독 삭제
    void deleteByUserIdAndSeriesId(Long userId, Long seriesId);
} 