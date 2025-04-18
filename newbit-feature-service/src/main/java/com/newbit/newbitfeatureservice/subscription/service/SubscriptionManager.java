package com.newbit.newbitfeatureservice.subscription.service;

import com.newbit.newbitfeatureservice.subscription.dto.response.SubscriptionResponse;
import com.newbit.newbitfeatureservice.subscription.entity.Subscription;
import com.newbit.newbitfeatureservice.subscription.entity.SubscriptionId;
import com.newbit.newbitfeatureservice.subscription.repository.SubscriptionRepository;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionManager {

    private final SubscriptionRepository subscriptionRepository;
    private final EntityManager entityManager;
    
    public SubscriptionResponse cancelSubscription(Subscription subscription) {
        SubscriptionId subscriptionId = new SubscriptionId(subscription.getUserId(), subscription.getSeriesId());
        SubscriptionResponse response = SubscriptionResponse.canceledSubscription(
                subscription.getSeriesId(), subscription.getUserId());
        
        subscriptionRepository.deleteById(subscriptionId);
        entityManager.flush();
        log.info("시리즈 구독 취소 (삭제): seriesId={}, userId={}", 
                subscription.getSeriesId(), subscription.getUserId());
        
        verifySubscriptionState(subscription.getUserId(), subscription.getSeriesId(), false, "구독 삭제 후에도 여전히 존재함");
        
        return response;
    }
    
    public SubscriptionResponse createNewSubscription(Long userId, Long seriesId) {
        Subscription newSubscription = Subscription.builder()
                .seriesId(seriesId)
                .userId(userId)
                .build();
        
        Subscription savedSubscription = subscriptionRepository.save(newSubscription);
        entityManager.flush();
        log.info("시리즈 새 구독: seriesId={}, userId={}", seriesId, userId);
        
        verifySubscriptionState(userId, seriesId, true, "구독 저장 후에도 조회되지 않음");
        
        return SubscriptionResponse.from(savedSubscription);
    }
   
    private void verifySubscriptionState(Long userId, Long seriesId, boolean expectedState, String errorMessage) {
        boolean currentState = subscriptionRepository.existsByUserIdAndSeriesId(userId, seriesId);
        if (currentState != expectedState) {
            log.warn("{}: seriesId={}, userId={}", errorMessage, seriesId, userId);
        }
    }
} 