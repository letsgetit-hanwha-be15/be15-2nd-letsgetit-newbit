package com.newbit.subscription.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newbit.column.repository.SeriesRepository;
import com.newbit.subscription.dto.response.SubscriptionResponse;
import com.newbit.subscription.dto.response.SubscriptionStatusResponse;
import com.newbit.subscription.entity.Subscription;
import com.newbit.subscription.entity.SubscriptionId;
import com.newbit.subscription.repository.SubscriptionRepository;
import com.newbit.subscription.util.SubscriptionValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final SeriesRepository seriesRepository;
    private final SubscriptionValidator validator;
    private final SubscriptionManager manager;

    @Transactional
    public SubscriptionResponse toggleSubscription(Long seriesId, Long userId) {
        try {
            validator.validateSeriesExists(seriesId, seriesRepository);
            
            SubscriptionId subscriptionId = new SubscriptionId(userId, seriesId);
            Optional<Subscription> existingSubscription = subscriptionRepository.findById(subscriptionId);
            
            if (existingSubscription.isPresent()) {
                return manager.cancelSubscription(existingSubscription.get());
            } else {
                return manager.createNewSubscription(userId, seriesId);
            }
        } catch (Exception e) {
            throw validator.handleSubscriptionError(e, "구독 토글 처리 중 오류 발생", seriesId, userId);
        }
    }
    
    @Transactional(readOnly = true)
    public SubscriptionStatusResponse getSubscriptionStatus(Long seriesId, Long userId) {
        try {
            validator.validateSeriesExists(seriesId, seriesRepository);
            
            boolean isSubscribed = subscriptionRepository.existsByUserIdAndSeriesId(userId, seriesId);
            int subscriberCount = subscriptionRepository.countBySeriesId(seriesId);
            
            return SubscriptionStatusResponse.of(seriesId, userId, isSubscribed, subscriberCount);
        } catch (Exception e) {
            throw validator.handleSubscriptionError(e, "구독 상태 조회 중 오류 발생", seriesId, userId);
        }
    }
    
    @Transactional(readOnly = true)
    public int getSubscriberCount(Long seriesId) {
        try {
            validator.validateSeriesExists(seriesId, seriesRepository);
            return subscriptionRepository.countBySeriesId(seriesId);
        } catch (Exception e) {
            throw validator.handleSubscriptionError(e, "구독자 수 조회 중 오류 발생", seriesId, null);
        }
    }
    
    @Transactional(readOnly = true)
    public List<SubscriptionResponse> getUserSubscriptions(Long userId) {
        try {
            return subscriptionRepository.findByUserId(userId).stream()
                    .map(SubscriptionResponse::from)
                    .toList();
        } catch (Exception e) {
            throw validator.handleSubscriptionError(e, "사용자 구독 목록 조회 중 오류 발생", null, userId);
        }
    }
    
    @Transactional(readOnly = true)
    public List<Long> getSeriesSubscribers(Long seriesId) {
        try {
            validator.validateSeriesExists(seriesId, seriesRepository);
            return subscriptionRepository.findBySeriesId(seriesId).stream()
                    .map(Subscription::getUserId)
                    .toList();
        } catch (Exception e) {
            throw validator.handleSubscriptionError(e, "시리즈 구독자 목록 조회 중 오류 발생", seriesId, null);
        }
    }
} 