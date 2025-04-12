package com.newbit.subscription.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.newbit.column.domain.Series;
import com.newbit.column.repository.SeriesRepository;
import com.newbit.common.exception.BusinessException;
import com.newbit.common.exception.ErrorCode;
import com.newbit.subscription.dto.response.SubscriptionResponse;
import com.newbit.subscription.dto.response.SubscriptionStatusResponse;
import com.newbit.subscription.entity.Subscription;
import com.newbit.subscription.entity.SubscriptionId;
import com.newbit.subscription.repository.SubscriptionRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("구독 서비스 단위 테스트")
class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private SeriesRepository seriesRepository;

    @InjectMocks
    private SubscriptionService subscriptionService;

    @Test
    @DisplayName("새 구독 추가 테스트")
    void addNewSubscriptionTest() {
        Long userId = 1L;
        Long seriesId = 1L;
        
        Series series = mock(Series.class);
        
        Subscription subscription = Subscription.builder()
                .userId(userId)
                .seriesId(seriesId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        SubscriptionId id = new SubscriptionId(userId, seriesId);
        
        when(seriesRepository.findById(seriesId)).thenReturn(Optional.of(series));
        when(subscriptionRepository.findById(id)).thenReturn(Optional.empty());
        when(subscriptionRepository.save(any(Subscription.class))).thenReturn(subscription);
        
        SubscriptionResponse response = subscriptionService.toggleSubscription(seriesId, userId);
        
        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo(userId);
        assertThat(response.getSeriesId()).isEqualTo(seriesId);
        assertThat(response.isSubscribed()).isTrue();
        
        verify(subscriptionRepository).save(any(Subscription.class));
        verify(subscriptionRepository, never()).deleteById(any(SubscriptionId.class));
    }
    
    @Test
    @DisplayName("기존 구독 취소 테스트")
    void cancelExistingSubscriptionTest() {
        Long userId = 1L;
        Long seriesId = 1L;
        
        Series series = mock(Series.class);
        
        Subscription subscription = Subscription.builder()
                .userId(userId)
                .seriesId(seriesId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        SubscriptionId id = new SubscriptionId(userId, seriesId);
        
        when(seriesRepository.findById(seriesId)).thenReturn(Optional.of(series));
        when(subscriptionRepository.findById(id)).thenReturn(Optional.of(subscription));
        
        SubscriptionResponse response = subscriptionService.toggleSubscription(seriesId, userId);
        
        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo(userId);
        assertThat(response.getSeriesId()).isEqualTo(seriesId);
        assertThat(response.isSubscribed()).isFalse();
        
        verify(subscriptionRepository, never()).save(any(Subscription.class));
        verify(subscriptionRepository).deleteById(any(SubscriptionId.class));
    }
    
    @Test
    @DisplayName("존재하지 않는 시리즈 구독 시도 시 예외 발생 테스트")
    void toggleSubscriptionWithNonExistingSeriesTest() {
        Long userId = 1L;
        Long nonExistingSeriesId = 999L;
        
        when(seriesRepository.findById(nonExistingSeriesId)).thenReturn(Optional.empty());
        
        BusinessException exception = assertThrows(BusinessException.class, () -> 
            subscriptionService.toggleSubscription(nonExistingSeriesId, userId)
        );
        
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.SERIES_NOT_FOUND);
        
        verify(subscriptionRepository, never()).save(any(Subscription.class));
        verify(subscriptionRepository, never()).deleteById(any(SubscriptionId.class));
    }
    
    @Test
    @DisplayName("구독 상태 및 구독자 수 조회 테스트")
    void getSubscriptionStatusTest() {
        Long userId = 1L;
        Long seriesId = 1L;
        int subscriberCount = 42;
        
        when(seriesRepository.existsById(seriesId)).thenReturn(true);
        when(subscriptionRepository.existsByUserIdAndSeriesId(userId, seriesId)).thenReturn(true);
        when(subscriptionRepository.countBySeriesId(seriesId)).thenReturn(subscriberCount);
        
        SubscriptionStatusResponse response = subscriptionService.getSubscriptionStatus(seriesId, userId);
        
        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo(userId);
        assertThat(response.getSeriesId()).isEqualTo(seriesId);
        assertThat(response.isSubscribed()).isTrue();
        assertThat(response.getTotalSubscribers()).isEqualTo(subscriberCount);
    }
    
    @Test
    @DisplayName("사용자의 구독 목록 조회 테스트")
    void getUserSubscriptionsTest() {
        Long userId = 1L;
        Long seriesId1 = 1L;
        Long seriesId2 = 2L;
        
        Subscription sub1 = Subscription.builder()
                .userId(userId)
                .seriesId(seriesId1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        Subscription sub2 = Subscription.builder()
                .userId(userId)
                .seriesId(seriesId2)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        List<Subscription> subscriptions = Arrays.asList(sub1, sub2);
        
        when(subscriptionRepository.findByUserId(userId)).thenReturn(subscriptions);
        
        List<SubscriptionResponse> responses = subscriptionService.getUserSubscriptions(userId);
        
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getSeriesId()).isEqualTo(seriesId1);
        assertThat(responses.get(1).getSeriesId()).isEqualTo(seriesId2);
        assertThat(responses.get(0).isSubscribed()).isTrue();
        assertThat(responses.get(1).isSubscribed()).isTrue();
    }
    
    @Test
    @DisplayName("시리즈의 구독자 목록 조회 테스트")
    void getSeriesSubscribersTest() {
        Long userId1 = 1L;
        Long userId2 = 2L;
        Long seriesId = 1L;
        
        Subscription sub1 = Subscription.builder()
                .userId(userId1)
                .seriesId(seriesId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        Subscription sub2 = Subscription.builder()
                .userId(userId2)
                .seriesId(seriesId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        List<Subscription> subscriptions = Arrays.asList(sub1, sub2);
        
        when(seriesRepository.existsById(seriesId)).thenReturn(true);
        when(subscriptionRepository.findBySeriesId(seriesId)).thenReturn(subscriptions);
        
        List<Long> subscribers = subscriptionService.getSeriesSubscribers(seriesId);
        
        assertThat(subscribers).hasSize(2);
        assertThat(subscribers).containsExactly(userId1, userId2);
    }
    
    @Test
    @DisplayName("존재하지 않는 시리즈 구독자 조회 시 예외 발생 테스트")
    void getSeriesSubscribersWithNonExistingSeriesTest() {
        Long nonExistingSeriesId = 999L;
        
        when(seriesRepository.existsById(nonExistingSeriesId)).thenReturn(false);
        
        BusinessException exception = assertThrows(BusinessException.class, () -> 
            subscriptionService.getSeriesSubscribers(nonExistingSeriesId)
        );
        
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.SERIES_NOT_FOUND);
        
        verify(subscriptionRepository, never()).findBySeriesId(anyLong());
    }
    
    @Test
    @DisplayName("구독 처리 중 예외 발생 테스트")
    void subscriptionProcessingErrorTest() {
        Long seriesId = 1L;
        Long userId = 1L;
        
        Series series = mock(Series.class);
        
        when(seriesRepository.findById(seriesId)).thenReturn(Optional.of(series));
        when(subscriptionRepository.findById(any(SubscriptionId.class))).thenReturn(Optional.empty());
        when(subscriptionRepository.save(any(Subscription.class))).thenThrow(new RuntimeException("데이터베이스 오류"));
        
        BusinessException exception = assertThrows(BusinessException.class, () -> 
            subscriptionService.toggleSubscription(seriesId, userId)
        );
        
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.SUBSCRIPTION_PROCESSING_ERROR);
    }
} 