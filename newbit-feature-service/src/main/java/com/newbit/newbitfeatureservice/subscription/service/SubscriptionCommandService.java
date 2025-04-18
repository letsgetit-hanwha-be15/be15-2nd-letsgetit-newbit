package com.newbit.newbitfeatureservice.subscription.service;

import com.newbit.newbitfeatureservice.client.user.MentorFeignClient;
import org.springframework.stereotype.Component;

import com.newbit.newbitfeatureservice.column.service.SeriesService;
import com.newbit.newbitfeatureservice.common.exception.BusinessException;
import com.newbit.newbitfeatureservice.common.exception.ErrorCode;
import com.newbit.newbitfeatureservice.subscription.dto.response.SubscriptionResponse;
import com.newbit.newbitfeatureservice.subscription.entity.Subscription;
import com.newbit.newbitfeatureservice.subscription.entity.SubscriptionId;
import com.newbit.newbitfeatureservice.subscription.repository.SubscriptionRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SubscriptionCommandService {

    private final SubscriptionRepository subscriptionRepository;
    private final MentorFeignClient mentorFeignClient;
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
                Long creatorUserId = mentorFeignClient.getUserIdByMentorId(mentorId).getData();
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