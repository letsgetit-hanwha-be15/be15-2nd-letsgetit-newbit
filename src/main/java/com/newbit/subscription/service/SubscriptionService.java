package com.newbit.subscription.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    /**
     * 시리즈 구독 토글
     * - 구독 중이면 취소 (삭제)
     * - 구독 중이지 않으면 구독 (추가)
     */
    @Transactional
    public SubscriptionResponse toggleSubscription(Long seriesId, Long userId) {
        try {
            // 1. 시리즈 존재 여부 확인
            Series series = seriesRepository.findById(seriesId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.SERIES_NOT_FOUND));
            
            // 2. 기존 구독 정보 조회
            SubscriptionId subscriptionId = new SubscriptionId(userId, seriesId);
            Optional<Subscription> existingSubscription = subscriptionRepository.findById(subscriptionId);
            
            // 3. 구독 토글 처리
            if (existingSubscription.isPresent()) {
                // 구독 취소 - 하드 딜리트
                Subscription subscription = existingSubscription.get();
                SubscriptionResponse response = SubscriptionResponse.canceledSubscription(subscription.getSeriesId(), subscription.getUserId());
                
                subscriptionRepository.deleteById(subscriptionId);
                log.info("시리즈 구독 취소 (삭제): seriesId={}, userId={}", seriesId, userId);
                
                return response;
            } else {
                // 새로운 구독 생성
                Subscription newSubscription = Subscription.builder()
                        .seriesId(seriesId)
                        .userId(userId)
                        .build();
                
                Subscription savedSubscription = subscriptionRepository.save(newSubscription);
                log.info("시리즈 새 구독: seriesId={}, userId={}", seriesId, userId);
                return SubscriptionResponse.from(savedSubscription);
            }
        } catch (Exception e) {
            if (e instanceof BusinessException) {
                throw e;
            }
            log.error("구독 토글 처리 중 오류 발생: seriesId={}, userId={}, error={}", seriesId, userId, e.getMessage());
            throw new BusinessException(ErrorCode.SUBSCRIPTION_PROCESSING_ERROR);
        }
    }
    
    /**
     * 구독 상태 및 구독자 수 조회
     */
    @Transactional(readOnly = true)
    public SubscriptionStatusResponse getSubscriptionStatus(Long seriesId, Long userId) {
        try {
            // 1. 시리즈 존재 여부 확인
            if (!seriesRepository.existsById(seriesId)) {
                throw new BusinessException(ErrorCode.SERIES_NOT_FOUND);
            }
            
            // 2. 구독 상태 확인
            boolean isSubscribed = subscriptionRepository.existsByUserIdAndSeriesId(userId, seriesId);
            
            // 3. 구독자 수 확인
            int subscriberCount = subscriptionRepository.countBySeriesId(seriesId);
            
            return SubscriptionStatusResponse.of(seriesId, userId, isSubscribed, subscriberCount);
        } catch (Exception e) {
            if (e instanceof BusinessException) {
                throw e;
            }
            log.error("구독 상태 조회 중 오류 발생: seriesId={}, userId={}, error={}", seriesId, userId, e.getMessage());
            throw new BusinessException(ErrorCode.SUBSCRIPTION_PROCESSING_ERROR);
        }
    }
    
    /**
     * 구독자 수 조회
     */
    @Transactional(readOnly = true)
    public int getSubscriberCount(Long seriesId) {
        try {
            // 시리즈 존재 여부 확인
            if (!seriesRepository.existsById(seriesId)) {
                throw new BusinessException(ErrorCode.SERIES_NOT_FOUND);
            }
            
            return subscriptionRepository.countBySeriesId(seriesId);
        } catch (Exception e) {
            if (e instanceof BusinessException) {
                throw e;
            }
            log.error("구독자 수 조회 중 오류 발생: seriesId={}, error={}", seriesId, e.getMessage());
            throw new BusinessException(ErrorCode.SUBSCRIPTION_PROCESSING_ERROR);
        }
    }
    
    /**
     * 사용자의 구독 목록 조회
     */
    @Transactional(readOnly = true)
    public List<SubscriptionResponse> getUserSubscriptions(Long userId) {
        try {
            return subscriptionRepository.findByUserId(userId).stream()
                    .map(SubscriptionResponse::from)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("사용자 구독 목록 조회 중 오류 발생: userId={}, error={}", userId, e.getMessage());
            throw new BusinessException(ErrorCode.SUBSCRIPTION_PROCESSING_ERROR);
        }
    }
    
    /**
     * 시리즈의 구독자 목록 조회
     */
    @Transactional(readOnly = true)
    public List<Long> getSeriesSubscribers(Long seriesId) {
        try {
            // 시리즈 존재 여부 확인
            if (!seriesRepository.existsById(seriesId)) {
                throw new BusinessException(ErrorCode.SERIES_NOT_FOUND);
            }
            
            return subscriptionRepository.findBySeriesId(seriesId).stream()
                    .map(Subscription::getUserId)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            if (e instanceof BusinessException) {
                throw e;
            }
            log.error("시리즈 구독자 목록 조회 중 오류 발생: seriesId={}, error={}", seriesId, e.getMessage());
            throw new BusinessException(ErrorCode.SUBSCRIPTION_PROCESSING_ERROR);
        }
    }
} 