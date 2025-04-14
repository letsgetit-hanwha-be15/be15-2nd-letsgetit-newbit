package com.newbit.subscription.service;

import org.springframework.stereotype.Component;

import com.newbit.column.domain.Series;
import com.newbit.column.repository.SeriesRepository;
import com.newbit.common.exception.BusinessException;
import com.newbit.common.exception.ErrorCode;
import com.newbit.subscription.dto.response.SubscriptionResponse;
import com.newbit.subscription.entity.Subscription;
import com.newbit.subscription.entity.SubscriptionId;
import com.newbit.subscription.repository.SubscriptionRepository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionCommandService {

    private final SubscriptionRepository subscriptionRepository;
    private final EntityManager entityManager;
    private final SeriesRepository seriesRepository;
    
    public SubscriptionResponse cancelSubscription(Subscription subscription) {
        try {
            SubscriptionId subscriptionId = new SubscriptionId(subscription.getUserId(), subscription.getSeriesId());
            SubscriptionResponse response = SubscriptionResponse.canceledSubscription(
                    subscription.getSeriesId(), subscription.getUserId());
            
            subscriptionRepository.deleteById(subscriptionId);
            entityManager.flush();
            log.info("시리즈 구독 취소 (삭제): seriesId={}, userId={}", 
                    subscription.getSeriesId(), subscription.getUserId());
            
            verifySubscriptionState(subscription.getUserId(), subscription.getSeriesId(), false, "구독 삭제 후에도 여전히 존재함");
            
            return response;
        } catch (Exception e) {
            log.error("구독 취소 중 오류 발생: seriesId={}, userId={}, error={}", 
                      subscription.getSeriesId(), subscription.getUserId(), e.getMessage(), e);
            throw new BusinessException(ErrorCode.SUBSCRIPTION_CANCEL_ERROR);
        }
    }
    
    public SubscriptionResponse createNewSubscription(Long userId, Long seriesId) {
        try {
            Series series = seriesRepository.findById(seriesId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.SERIES_NOT_FOUND));
            
            if (series.getMentor() != null && 
                series.getMentor().getUser() != null && 
                series.getMentor().getUser().getUserId() != null && 
                series.getMentor().getUser().getUserId().equals(userId)) {
                throw new BusinessException(ErrorCode.SUBSCRIPTION_SELF_NOT_ALLOWED);
            }
            
            if (subscriptionRepository.existsByUserIdAndSeriesId(userId, seriesId)) {
                throw new BusinessException(ErrorCode.SUBSCRIPTION_ALREADY_EXISTS);
            }
            
            Subscription newSubscription = Subscription.builder()
                    .seriesId(seriesId)
                    .userId(userId)
                    .build();
            
            Subscription savedSubscription = subscriptionRepository.save(newSubscription);
            entityManager.flush();
            log.info("시리즈 새 구독: seriesId={}, userId={}", seriesId, userId);
            
            verifySubscriptionState(userId, seriesId, true, "구독 저장 후에도 조회되지 않음");
            
            return SubscriptionResponse.from(savedSubscription);
        } catch (BusinessException e) {
            log.error("구독 생성 중 비즈니스 예외 발생: seriesId={}, userId={}, errorCode={}, message={}", 
                    seriesId, userId, e.getErrorCode(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("구독 생성 중 예기치 않은 오류 발생: seriesId={}, userId={}, error={}", 
                    seriesId, userId, e.getMessage(), e);
            throw new BusinessException(ErrorCode.SUBSCRIPTION_ERROR);
        }
    }
   
    private void verifySubscriptionState(Long userId, Long seriesId, boolean expectedState, String errorMessage) {
        boolean currentState = subscriptionRepository.existsByUserIdAndSeriesId(userId, seriesId);
        if (currentState != expectedState) {
            log.warn("{}: seriesId={}, userId={}", errorMessage, seriesId, userId);
        }
    }
} 