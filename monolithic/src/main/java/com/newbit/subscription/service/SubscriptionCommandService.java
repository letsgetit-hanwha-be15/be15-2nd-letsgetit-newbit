package com.newbit.subscription.service;

import org.springframework.stereotype.Component;

import com.newbit.column.service.SeriesService;
import com.newbit.common.exception.BusinessException;
import com.newbit.common.exception.ErrorCode;
import com.newbit.subscription.dto.response.SubscriptionResponse;
import com.newbit.subscription.entity.Subscription;
import com.newbit.subscription.entity.SubscriptionId;
import com.newbit.subscription.repository.SubscriptionRepository;
import com.newbit.user.service.MentorService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SubscriptionCommandService {

    private final SubscriptionRepository subscriptionRepository;
    private final MentorService mentorService;
    private final SeriesService seriesService;
    
    public SubscriptionResponse cancelSubscription(Subscription subscription) {
        SubscriptionId subscriptionId = subscription.getId();
        SubscriptionResponse response = SubscriptionResponse.canceledSubscription(
                subscription.getSeriesId(), subscription.getUserId());
        
        subscriptionRepository.deleteById(subscriptionId);
        
        return response;
    }
    
    public SubscriptionResponse createNewSubscription(Long userId, Long seriesId) {
        Long mentorId = seriesService.getSeries(seriesId).getMentorId();
        
        if (mentorId != null) {
            try {
                Long creatorUserId = mentorService.getUserIdByMentorId(mentorId);
                if (creatorUserId != null && creatorUserId.equals(userId)) {
                    throw new BusinessException(ErrorCode.SUBSCRIPTION_SELF_NOT_ALLOWED);
                }
            } catch (BusinessException e) {
                throw e;
            } catch (Exception e) {
            }
        }
        
        if (subscriptionRepository.existsByUserIdAndSeriesId(userId, seriesId)) {
            throw new BusinessException(ErrorCode.SUBSCRIPTION_ALREADY_EXISTS);
        }
        
        Subscription newSubscription = Subscription.createSubscription(userId, seriesId);
        Subscription savedSubscription = subscriptionRepository.save(newSubscription);
        
        return SubscriptionResponse.from(savedSubscription);
    }
} 