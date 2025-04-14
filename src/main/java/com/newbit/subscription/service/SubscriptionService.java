package com.newbit.subscription.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newbit.column.domain.Series;
import com.newbit.column.repository.SeriesRepository;
import com.newbit.common.exception.BusinessException;
import com.newbit.common.exception.ErrorCode;
import com.newbit.subscription.dto.response.SubscriptionResponse;
import com.newbit.subscription.dto.response.SubscriptionStatusResponse;
import com.newbit.subscription.entity.Subscription;
import com.newbit.subscription.entity.SubscriptionId;
import com.newbit.subscription.repository.SubscriptionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final SeriesRepository seriesRepository;
    private final SubscriptionCommandService manager;

    private void validateSeriesExists(Long seriesId) {
        seriesRepository.findById(seriesId)
                .orElseThrow(() -> {
                    log.error("시리즈를 찾을 수 없음: seriesId={}", seriesId);
                    return new BusinessException(ErrorCode.SERIES_FOR_SUBSCRIPTION_NOT_FOUND);
                });
    }

    @Transactional
    public SubscriptionResponse toggleSubscription(Long seriesId, Long userId) {
        try {
            validateSeriesExists(seriesId);
            
            SubscriptionId subscriptionId = new SubscriptionId(userId, seriesId);
            Optional<Subscription> existingSubscription = subscriptionRepository.findById(subscriptionId);
            
            if (existingSubscription.isPresent()) {
                return manager.cancelSubscription(existingSubscription.get());
            } else {
                return manager.createNewSubscription(userId, seriesId);
            }
        } catch (BusinessException e) {
            log.error("구독 토글 처리 중 비즈니스 예외 발생: seriesId={}, userId={}, errorCode={}, message={}", 
                    seriesId, userId, e.getErrorCode(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("구독 토글 처리 중 예기치 않은 오류 발생: seriesId={}, userId={}, error={}", 
                    seriesId, userId, e.getMessage(), e);
            throw new BusinessException(ErrorCode.SUBSCRIPTION_ERROR);
        }
    }
    
    @Transactional(readOnly = true)
    public SubscriptionStatusResponse getSubscriptionStatus(Long seriesId, Long userId) {
        try {
            validateSeriesExists(seriesId);
            
            boolean isSubscribed = subscriptionRepository.existsByUserIdAndSeriesId(userId, seriesId);
            int subscriberCount = subscriptionRepository.countBySeriesId(seriesId);
            
            return SubscriptionStatusResponse.of(seriesId, userId, isSubscribed, subscriberCount);
        } catch (BusinessException e) {
            log.error("구독 상태 조회 중 비즈니스 예외 발생: seriesId={}, userId={}, errorCode={}, message={}", 
                    seriesId, userId, e.getErrorCode(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("구독 상태 조회 중 예기치 않은 오류 발생: seriesId={}, userId={}, error={}", 
                    seriesId, userId, e.getMessage(), e);
            throw new BusinessException(ErrorCode.SUBSCRIPTION_ERROR);
        }
    }
    
    @Transactional(readOnly = true)
    public int getSubscriberCount(Long seriesId) {
        try {
            validateSeriesExists(seriesId);
            return subscriptionRepository.countBySeriesId(seriesId);
        } catch (BusinessException e) {
            log.error("구독자 수 조회 중 비즈니스 예외 발생: seriesId={}, errorCode={}, message={}", 
                    seriesId, e.getErrorCode(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("구독자 수 조회 중 예기치 않은 오류 발생: seriesId={}, error={}", 
                    seriesId, e.getMessage(), e);
            throw new BusinessException(ErrorCode.SUBSCRIPTION_ERROR);
        }
    }
    
    @Transactional(readOnly = true)
    public List<SubscriptionResponse> getUserSubscriptions(Long userId) {
        try {
            return subscriptionRepository.findByUserId(userId).stream()
                    .map(SubscriptionResponse::from)
                    .toList();
        } catch (BusinessException e) {
            log.error("사용자 구독 목록 조회 중 비즈니스 예외 발생: userId={}, errorCode={}, message={}", 
                    userId, e.getErrorCode(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("사용자 구독 목록 조회 중 예기치 않은 오류 발생: userId={}, error={}", 
                    userId, e.getMessage(), e);
            throw new BusinessException(ErrorCode.SUBSCRIPTION_ERROR);
        }
    }
    
    @Transactional(readOnly = true)
    public List<Long> getSeriesSubscribers(Long seriesId) {
        try {
            validateSeriesExists(seriesId);
            return subscriptionRepository.findBySeriesId(seriesId).stream()
                    .map(Subscription::getUserId)
                    .toList();
        } catch (BusinessException e) {
            log.error("시리즈 구독자 목록 조회 중 비즈니스 예외 발생: seriesId={}, errorCode={}, message={}", 
                    seriesId, e.getErrorCode(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("시리즈 구독자 목록 조회 중 예기치 않은 오류 발생: seriesId={}, error={}", 
                    seriesId, e.getMessage(), e);
            throw new BusinessException(ErrorCode.SUBSCRIPTION_ERROR);
        }
    }
} 