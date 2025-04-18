package com.newbit.newbitfeatureservice.subscription.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.newbit.newbitfeatureservice.subscription.entity.Subscription;
import com.newbit.newbitfeatureservice.subscription.entity.SubscriptionId;

public interface SubscriptionRepository extends JpaRepository<Subscription, SubscriptionId> {
    
    List<Subscription> findByUserId(Long userId);
    
    List<Subscription> findBySeriesId(Long seriesId);
    
    boolean existsByUserIdAndSeriesId(Long userId, Long seriesId);
    
    int countBySeriesId(Long seriesId);
}