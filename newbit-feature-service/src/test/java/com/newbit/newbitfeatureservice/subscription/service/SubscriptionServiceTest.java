package com.newbit.newbitfeatureservice.subscription.service;

import com.newbit.newbitfeatureservice.column.domain.Series;
import com.newbit.newbitfeatureservice.column.service.SeriesService;
import com.newbit.newbitfeatureservice.common.exception.BusinessException;
import com.newbit.newbitfeatureservice.common.exception.ErrorCode;
import com.newbit.newbitfeatureservice.subscription.dto.response.SubscriptionResponse;
import com.newbit.newbitfeatureservice.subscription.dto.response.SubscriptionStatusResponse;
import com.newbit.newbitfeatureservice.subscription.entity.Subscription;
import com.newbit.newbitfeatureservice.subscription.entity.SubscriptionId;
import com.newbit.newbitfeatureservice.subscription.repository.SubscriptionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("구독 서비스 단위 테스트")
class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private SeriesService seriesService;
    
    @Mock
    private SubscriptionCommandService subscriptionCommandService;

    @InjectMocks
    private SubscriptionService subscriptionService;

    private Series mockSeries() {
        return mock(Series.class);
    }
    
    private void setupSeriesValidation(Long seriesId) {
        when(seriesService.getSeries(seriesId)).thenReturn(mockSeries());
    }
    
    private Subscription createSubscription(Long userId, Long seriesId) {
        return Subscription.builder()
                .userId(userId)
                .seriesId(seriesId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("새 구독 추가 테스트")
    void addNewSubscriptionTest() {
        // Given
        Long userId = 1L;
        Long seriesId = 1L;
        
        setupSeriesValidation(seriesId);
        SubscriptionId id = new SubscriptionId(userId, seriesId);
        SubscriptionResponse expectedResponse = SubscriptionResponse.from(createSubscription(userId, seriesId));
        
        when(subscriptionRepository.findById(id)).thenReturn(Optional.empty());
        when(subscriptionCommandService.createNewSubscription(userId, seriesId)).thenReturn(expectedResponse);
        
        // When
        SubscriptionResponse response = subscriptionService.toggleSubscription(seriesId, userId);
        
        // Then
        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo(userId);
        assertThat(response.getSeriesId()).isEqualTo(seriesId);
        assertThat(response.isSubscribed()).isTrue();
        
        verify(subscriptionCommandService).createNewSubscription(userId, seriesId);
        verify(subscriptionCommandService, never()).cancelSubscription(any());
    }
    
    @Test
    @DisplayName("기존 구독 취소 테스트")
    void cancelExistingSubscriptionTest() {
        // Given
        Long userId = 1L;
        Long seriesId = 1L;
        
        setupSeriesValidation(seriesId);
        Subscription subscription = createSubscription(userId, seriesId);
        SubscriptionId id = new SubscriptionId(userId, seriesId);
        SubscriptionResponse expectedResponse = SubscriptionResponse.canceledSubscription(seriesId, userId);
        
        when(subscriptionRepository.findById(id)).thenReturn(Optional.of(subscription));
        when(subscriptionCommandService.cancelSubscription(subscription)).thenReturn(expectedResponse);
        
        // When
        SubscriptionResponse response = subscriptionService.toggleSubscription(seriesId, userId);
        
        // Then
        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo(userId);
        assertThat(response.getSeriesId()).isEqualTo(seriesId);
        assertThat(response.isSubscribed()).isFalse();
        
        verify(subscriptionCommandService).cancelSubscription(subscription);
        verify(subscriptionCommandService, never()).createNewSubscription(anyLong(), anyLong());
    }
    
    @Test
    @DisplayName("존재하지 않는 시리즈 구독 시도 시 예외 발생 테스트")
    void toggleSubscriptionWithNonExistingSeriesTest() {
        // Given
        Long userId = 1L;
        Long nonExistingSeriesId = 999L;
        
        when(seriesService.getSeries(nonExistingSeriesId)).thenThrow(new BusinessException(ErrorCode.SERIES_NOT_FOUND));
        
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> 
            subscriptionService.toggleSubscription(nonExistingSeriesId, userId)
        );
        
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.SERIES_NOT_FOUND);
        
        verify(subscriptionCommandService, never()).createNewSubscription(anyLong(), anyLong());
        verify(subscriptionCommandService, never()).cancelSubscription(any());
    }
    
    @Test
    @DisplayName("구독 상태 및 구독자 수 조회 테스트")
    void getSubscriptionStatusTest() {
        // Given
        Long userId = 1L;
        Long seriesId = 1L;
        int subscriberCount = 42;
        
        setupSeriesValidation(seriesId);
        
        when(subscriptionRepository.existsByUserIdAndSeriesId(userId, seriesId)).thenReturn(true);
        when(subscriptionRepository.countBySeriesId(seriesId)).thenReturn(subscriberCount);
        
        // When
        SubscriptionStatusResponse response = subscriptionService.getSubscriptionStatus(seriesId, userId);
        
        // Then
        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo(userId);
        assertThat(response.getSeriesId()).isEqualTo(seriesId);
        assertThat(response.isSubscribed()).isTrue();
        assertThat(response.getTotalSubscribers()).isEqualTo(subscriberCount);
    }
    
    @Test
    @DisplayName("사용자의 구독 목록 조회 테스트")
    void getUserSubscriptionsTest() {
        // Given
        Long userId = 1L;
        Long seriesId1 = 1L;
        Long seriesId2 = 2L;
        
        Subscription sub1 = createSubscription(userId, seriesId1);
        Subscription sub2 = createSubscription(userId, seriesId2);
        
        List<Subscription> subscriptions = Arrays.asList(sub1, sub2);
        
        when(subscriptionRepository.findByUserId(userId)).thenReturn(subscriptions);
        
        // When
        List<SubscriptionResponse> responses = subscriptionService.getUserSubscriptions(userId);
        
        // Then
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getSeriesId()).isEqualTo(seriesId1);
        assertThat(responses.get(1).getSeriesId()).isEqualTo(seriesId2);
        assertThat(responses.get(0).isSubscribed()).isTrue();
        assertThat(responses.get(1).isSubscribed()).isTrue();
    }
    
    @Test
    @DisplayName("시리즈의 구독자 목록 조회 테스트")
    void getSeriesSubscribersTest() {
        // Given
        Long userId1 = 1L;
        Long userId2 = 2L;
        Long seriesId = 1L;
        
        setupSeriesValidation(seriesId);
        
        Subscription sub1 = createSubscription(userId1, seriesId);
        Subscription sub2 = createSubscription(userId2, seriesId);
        
        List<Subscription> subscriptions = Arrays.asList(sub1, sub2);
        
        when(subscriptionRepository.findBySeriesId(seriesId)).thenReturn(subscriptions);
        
        // When
        List<Long> subscribers = subscriptionService.getSeriesSubscribers(seriesId);
        
        // Then
        assertThat(subscribers).hasSize(2);
        assertThat(subscribers).containsExactly(userId1, userId2);
    }
    
    @Test
    @DisplayName("존재하지 않는 시리즈 구독자 조회 시 예외 발생 테스트")
    void getSeriesSubscribersWithNonExistingSeriesTest() {
        // Given
        Long nonExistingSeriesId = 999L;
        
        when(seriesService.getSeries(nonExistingSeriesId)).thenThrow(new BusinessException(ErrorCode.SERIES_NOT_FOUND));
        
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> 
            subscriptionService.getSeriesSubscribers(nonExistingSeriesId)
        );
        
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.SERIES_NOT_FOUND);
        
        verify(subscriptionRepository, never()).findBySeriesId(anyLong());
    }
    
    @Test
    @DisplayName("구독 처리 중 예외 발생 테스트")
    void subscriptionProcessingErrorTest() {
        // Given
        Long userId = 1L;
        Long seriesId = 1L;
        
        setupSeriesValidation(seriesId);
        RuntimeException runtimeException = new RuntimeException("Unexpected error");
        
        when(subscriptionRepository.findById(any())).thenThrow(runtimeException);
        
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> 
            subscriptionService.toggleSubscription(seriesId, userId)
        );
        
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.SUBSCRIPTION_ERROR);
        
        verify(subscriptionCommandService, never()).createNewSubscription(anyLong(), anyLong());
        verify(subscriptionCommandService, never()).cancelSubscription(any());
    }
} 