package com.newbit.newbitfeatureservice.subscription.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newbit.newbitfeatureservice.column.service.SeriesService;
import com.newbit.newbitfeatureservice.common.exception.BusinessException;
import com.newbit.newbitfeatureservice.common.exception.ErrorCode;
import com.newbit.newbitfeatureservice.subscription.dto.response.SubscriptionResponse;
import com.newbit.newbitfeatureservice.subscription.dto.response.SubscriptionStatusResponse;
import com.newbit.newbitfeatureservice.subscription.entity.Subscription;
import com.newbit.newbitfeatureservice.subscription.entity.SubscriptionId;
import com.newbit.newbitfeatureservice.subscription.repository.SubscriptionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final SeriesService seriesService;
    private final SubscriptionCommandService subscriptionCommandService;

    @Transactional
    public SubscriptionResponse toggleSubscription(Long seriesId, Long userId) {
        try {
            seriesService.getSeries(seriesId);
            
            SubscriptionId subscriptionId = new SubscriptionId(userId, seriesId);
            Optional<Subscription> existingSubscription = subscriptionRepository.findById(subscriptionId);
            
            if (existingSubscription.isPresent()) {
                return subscriptionCommandService.cancelSubscription(existingSubscription.get());
            } else {
                return subscriptionCommandService.createNewSubscription(userId, seriesId);
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SUBSCRIPTION_ERROR);
        }
    }
    
    @Transactional(readOnly = true)
    public SubscriptionStatusResponse getSubscriptionStatus(Long seriesId, Long userId) {
        seriesService.getSeries(seriesId);
        
        boolean isSubscribed = subscriptionRepository.existsByUserIdAndSeriesId(userId, seriesId);
        int subscriberCount = subscriptionRepository.countBySeriesId(seriesId);
        
        return SubscriptionStatusResponse.of(seriesId, userId, isSubscribed, subscriberCount);
    }
    
    @Transactional(readOnly = true)
    public int getSubscriberCount(Long seriesId) {
        seriesService.getSeries(seriesId);
        return subscriptionRepository.countBySeriesId(seriesId);
    }
    
    @Transactional(readOnly = true)
    public List<SubscriptionResponse> getUserSubscriptions(Long userId) {
        return subscriptionRepository.findByUserId(userId).stream()
                .map(SubscriptionResponse::from)
                .toList();
    }
    
    @Transactional(readOnly = true)
    public List<Long> getSeriesSubscribers(Long seriesId) {
        seriesService.getSeries(seriesId);
        return subscriptionRepository.findBySeriesId(seriesId).stream()
                .map(Subscription::getUserId)
                .toList();
    }
} 